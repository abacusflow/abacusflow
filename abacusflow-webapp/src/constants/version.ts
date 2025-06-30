export const CURRENT_VERSION = import.meta.env.VITE_ABACUSFLOW_VERSION;

export interface VersionAnnouncement {
  version: string;
  date: string;
  content: string[];
}
