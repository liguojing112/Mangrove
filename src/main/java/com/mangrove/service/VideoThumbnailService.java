package com.mangrove.service;

import com.mangrove.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoThumbnailService implements ApplicationRunner {

    private static final String THUMBNAIL_SUFFIX = ".poster.jpg";
    private static final long GENERATION_TIMEOUT_SECONDS = 45;

    private final FileUploadConfig fileUploadConfig;

    @Override
    public void run(ApplicationArguments args) {
        Path uploadDir = getUploadDir();
        if (!Files.isDirectory(uploadDir)) {
            return;
        }

        try (Stream<Path> files = Files.list(uploadDir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> isVideoFilename(path.getFileName().toString()))
                    .forEach(this::generateThumbnail);
        } catch (IOException e) {
            log.warn("扫描现有视频封面失败: {}", e.getMessage());
        }
    }

    public boolean isVideoFilename(String filename) {
        if (filename == null) {
            return false;
        }
        String lower = filename.toLowerCase(Locale.ROOT);
        return lower.endsWith(".mp4")
                || lower.endsWith(".webm")
                || lower.endsWith(".mov")
                || lower.endsWith(".avi");
    }

    public String generateThumbnail(Path videoPath) {
        if (videoPath == null || !Files.isRegularFile(videoPath)
                || !isVideoFilename(videoPath.getFileName().toString())) {
            return null;
        }

        Path thumbnailPath = thumbnailPath(videoPath);
        if (Files.isRegularFile(thumbnailPath)) {
            return toPublicUrl(thumbnailPath);
        }

        Process process = null;
        try {
            process = new ProcessBuilder(
                    "ffmpeg",
                    "-hide_banner",
                    "-loglevel", "error",
                    "-y",
                    "-ss", "0.5",
                    "-i", videoPath.toString(),
                    "-frames:v", "1",
                    "-q:v", "3",
                    thumbnailPath.toString())
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();

            boolean finished = process.waitFor(GENERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                Files.deleteIfExists(thumbnailPath);
                log.warn("生成视频封面超时: {}", videoPath.getFileName());
                return null;
            }
            if (process.exitValue() != 0 || !Files.isRegularFile(thumbnailPath)) {
                Files.deleteIfExists(thumbnailPath);
                log.warn("生成视频封面失败: {}", videoPath.getFileName());
                return null;
            }
            log.info("视频封面已生成: {}", thumbnailPath.getFileName());
            return toPublicUrl(thumbnailPath);
        } catch (IOException e) {
            log.warn("无法生成视频封面，请确认 FFmpeg 已安装: {}", e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            return null;
        }
    }

    public String getThumbnailUrl(String filename) {
        if (!isVideoFilename(filename)) {
            return null;
        }
        Path videoPath = getUploadDir().resolve(Paths.get(filename).getFileName().toString());
        Path thumbnailPath = thumbnailPath(videoPath);
        return Files.isRegularFile(thumbnailPath) ? toPublicUrl(thumbnailPath) : null;
    }

    public void deleteThumbnail(String filename) {
        if (!isVideoFilename(filename)) {
            return;
        }
        try {
            Path videoPath = getUploadDir().resolve(Paths.get(filename).getFileName().toString());
            Files.deleteIfExists(thumbnailPath(videoPath));
        } catch (IOException e) {
            log.warn("删除视频封面失败: {}", filename);
        }
    }

    private Path getUploadDir() {
        return Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize();
    }

    private Path thumbnailPath(Path videoPath) {
        return videoPath.resolveSibling(videoPath.getFileName() + THUMBNAIL_SUFFIX);
    }

    private String toPublicUrl(Path thumbnailPath) {
        return "/uploads/" + thumbnailPath.getFileName();
    }
}
