// squirrel.js
export const handleSquirrelStartup = async () => {
  try {
    const squirrelStartup = await import('electron-squirrel-startup');
    if (squirrelStartup.default) {
      process.exit(0);
    }
  } catch (e) {
    console.error('Failed to load electron-squirrel-startup:', e);
  }
};
