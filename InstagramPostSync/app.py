from flask import Flask, request, jsonify
import instaloader
import os
from minio import Minio
from minio.error import S3Error

app = Flask(__name__)
loader = instaloader.Instaloader()

minio_url = os.getenv("MINIO_URL", "localhost:9000")
minio_access_key = os.getenv("MINIO_ACCESS_KEY", "minio_user")
minio_secret_key = os.getenv("MINIO_SECRET_KEY", "minio_password")
bucket_name = os.getenv("BUCKET_NAME", "instagram-content")
download_dir = os.getenv("DOWNLOAD_DIR", "downloads")
print("START")
print(minio_url)
port = int(os.getenv("PORT", "5003"))

minio_client = Minio(
    minio_url,
    access_key=minio_access_key,
    secret_key=minio_secret_key,
    secure=False
)
print("CREATED")
# Ensure the MinIO bucket exists
if not minio_client.bucket_exists(bucket_name):
    minio_client.make_bucket(bucket_name)
    print(f"Bucket '{bucket_name}' created.")

os.makedirs(download_dir, exist_ok=True)


@app.route("/download", methods=["POST"])
def download_instagram_content():
    print("STARTED DDD")
    try:
        data = request.get_json()
        if not data or "url" not in data:
            return jsonify({"error": "URL is required"}), 400

        url = data["url"]
        shortcode = url.split("/")[-2]
        target_path = os.path.join(download_dir, shortcode)

        os.makedirs(target_path, exist_ok=True)

        download_media(url, target_path)

        upload_folder_to_minio(target_path, shortcode)

        return jsonify({
            "message": "All files downloaded and uploaded to MinIO successfully.",
            "shortcode": shortcode
        }), 200
        # return jsonify({"message": "All files downloaded and uploaded to MinIO successfully."}), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500


def download_media(url, target_path):
    try:
        shortcode = url.split("/")[-2]
        post = instaloader.Post.from_shortcode(loader.context, shortcode)
        loader.dirname_pattern = target_path
        loader.download_post(post, target="")
    except Exception as e:
        print(f"Error downloading media: {e}")
        raise


def upload_folder_to_minio(folder_path, shortcode):
    try:
        for root, _, files in os.walk(folder_path):
            for file in files:
                file_path = os.path.join(root, file)
                object_name = f"{shortcode}/{os.path.relpath(file_path, folder_path)}"

                with open(file_path, 'rb') as file_data:
                    minio_client.put_object(
                        bucket_name,
                        object_name,
                        file_data,
                        length=os.path.getsize(file_path),
                        content_type="application/octet-stream"
                    )
                print(f"File '{file_path}' uploaded to MinIO as '{object_name}'.")

                os.remove(file_path)
                print(f"File '{file_path}' deleted after upload.")
        os.rmdir(folder_path)
        print(f"Folder '{folder_path}' deleted after upload.")
    except S3Error as e:
        print(f"Error uploading to MinIO: {e}")
        raise
    except Exception as e:
        print(f"Error: {e}")
        raise


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=port)
