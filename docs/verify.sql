SELECT id, title, category, status, is_deleted, LEFT(file_url, 50) as file_path FROM media_assets ORDER BY id;
