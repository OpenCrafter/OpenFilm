import asyncio
from glob import glob
import html
import os
import sys
from telethon import TelegramClient


def get_required_env(name: str) -> str:
    value = os.environ.get(name)
    if not value:
        print(f"Error: Environment variable '{name}' is required but not set.")
        sys.exit(1)
    return value


async def main():
    api_id_str = get_required_env("API_ID")
    api_hash = get_required_env("API_HASH")
    bot_token = get_required_env("BOT_TOKEN")
    chat_id_raw = get_required_env("CHAT_ID")
    topic_id_raw = os.environ.get("TOPIC_ID")
    commit_author = os.environ.get("COMMIT_AUTHOR", "Unknown")
    commit_message = os.environ.get("COMMIT_MESSAGE", "No commit message")
    short_sha = os.environ.get("COMMIT_SHA", "")

    try:
        api_id = int(api_id_str)
    except ValueError:
        print("Error: API_ID must be a valid integer.")
        sys.exit(1)

    try:
        chat_id = int(chat_id_raw)
    except ValueError:
        chat_id = chat_id_raw

    reply_to = None
    if topic_id_raw:
        try:
            reply_to = int(topic_id_raw)
        except ValueError:
            print(f"Warning: TOPIC_ID '{topic_id_raw}' is not an integer. Ignoring topic reply.")

    artifact_dir = os.environ.get("ARTIFACT_DIR", "./downloaded-artifact")
    apk_files = glob(os.path.join(artifact_dir, "**/*.apk"), recursive=True)
    if not apk_files:
        apk_files = glob(os.path.join(artifact_dir, "*.apk"))

    if not apk_files:
        print(f"Error: No APK files found in '{artifact_dir}'.")
        sys.exit(1)

    escaped_author = html.escape(commit_author)
    escaped_message = html.escape(commit_message)

    session_name = os.environ.get("SESSION_NAME", "bot_session")
    client = TelegramClient(session_name, api_id, api_hash)
    await client.start(bot_token=bot_token)

    async with client:
        for apk_path in sorted(apk_files):
            filename = os.path.basename(apk_path)
            sha_line = f"🔖 <b>Commit:</b> <code>{html.escape(short_sha)}</code>\n" if short_sha else ""
            caption = (
                f"📦 <b>New Build: {html.escape(filename)}</b>\n\n"
                f"{sha_line}"
                f"👤 <b>Author:</b> {escaped_author}\n"
                f"💬 <b>Message:</b> {escaped_message}"
            )
            print(f"Uploading {apk_path} to chat {chat_id} (topic: {reply_to})...")
            send_kwargs = {
                "caption": caption,
                "parse_mode": "html",
            }
            if reply_to is not None:
                send_kwargs["reply_to"] = reply_to

            await client.send_file(chat_id, apk_path, **send_kwargs)
            print(f"Successfully uploaded {filename}!")


if __name__ == "__main__":
    asyncio.run(main())
