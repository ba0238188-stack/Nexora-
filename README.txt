Nexora — Major Final Update

Upload ALL files in this folder to the same Cloudflare static site.

Included:
- index.html — complete Nexora app
- manifest.json — PWA metadata + PNG app icons
- sw.js — versioned service worker with update/cache handling
- icon-192.png / icon-512.png / favicon.png / apple-touch-icon.png
- icon.svg

Main update:
- Persistent Supabase session: refresh/update does not require login again.
- Followed users' Stories only; unfollow removes their Stories from your Story tray.
- Block list in Settings + unblock flow.
- Save posts + Saved screen.
- Hide / Not interested posts (stored in DB).
- Post edit/delete for your own posts.
- Story delete for your own Stories.
- Reels playback and view tracking.
- Chat images/videos with instant local preview while uploading.
- Chat typing indicator + read state + online/last seen.
- Notifications + optional browser notifications while the app is open.
- Private accounts, reports, admin/owner tools, analytics and moderation retained.
- Draft text is preserved locally while composing a post.
- Service worker cache version bumped so a deployed update replaces the old cached UI.

Supabase project is already configured; no secret/service-role key is included.
