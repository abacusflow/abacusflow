export function capitalize(str?: string): string {
  if (!str) return "";

  return str.charAt(0).toUpperCase() + str.slice(1);
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $capitalize: (str: string) => string;
  }
}
