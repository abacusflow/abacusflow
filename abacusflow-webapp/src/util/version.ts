import { CURRENT_VERSION } from "@/constants/version";

const STORAGE_KEY = "lastReadVersion";

export function shouldShowAnnouncement(): boolean {
  return localStorage.getItem(STORAGE_KEY) !== CURRENT_VERSION;
}

export function markAnnouncementAsRead() {
  localStorage.setItem(STORAGE_KEY, CURRENT_VERSION);
}
