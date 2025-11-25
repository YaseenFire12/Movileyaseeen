// Helper validation functions

export const validateString = (str, fieldName) => {
  if (!str) throw new Error(`${fieldName} must be provided`);
  if (typeof str !== 'string') throw new Error(`${fieldName} must be a string`);
  str = str.trim();
  if (str.length === 0) throw new Error(`${fieldName} cannot be empty or just spaces`);
  return str;
};

export const validateName = (name, fieldName) => {
  name = validateString(name, fieldName);
  
  // Must be letters only, no spaces, no numbers
  if (!/^[a-zA-Z]+$/.test(name)) {
    throw new Error(`${fieldName} must contain only letters`);
  }
  
  if (name.length < 2 || name.length > 20) {
    throw new Error(`${fieldName} must be between 2 and 20 characters`);
  }
  
  return name;
};

export const validateUserId = (userId) => {
  userId = validateString(userId, 'userId');
  
  // Must be alphanumeric only
  if (!/^[a-zA-Z0-9]+$/.test(userId)) {
    throw new Error('userId must contain only letters and numbers');
  }
  
  if (userId.length < 5 || userId.length > 10) {
    throw new Error('userId must be between 5 and 10 characters');
  }
  
  // Return lowercase for case-insensitive comparison
  return userId.toLowerCase();
};

export const validatePassword = (password) => {
  if (!password || typeof password !== 'string') {
    throw new Error('Password must be provided and must be a string');
  }
  
  // No spaces allowed
  if (password.includes(' ')) {
    throw new Error('Password cannot contain spaces');
  }
  
  if (password.length < 8) {
    throw new Error('Password must be at least 8 characters long');
  }
  
  // Must have at least one uppercase letter
  if (!/[A-Z]/.test(password)) {
    throw new Error('Password must contain at least one uppercase letter');
  }
  
  // Must have at least one number
  if (!/[0-9]/.test(password)) {
    throw new Error('Password must contain at least one number');
  }
  
  // Must have at least one special character
  if (!/[^a-zA-Z0-9]/.test(password)) {
    throw new Error('Password must contain at least one special character');
  }
  
  return password;
};

export const validateQuote = (quote) => {
  quote = validateString(quote, 'favoriteQuote');
  
  if (quote.length < 20 || quote.length > 255) {
    throw new Error('favoriteQuote must be between 20 and 255 characters');
  }
  
  return quote;
};

export const validateHexColor = (color, fieldName) => {
  if (!color || typeof color !== 'string') {
    throw new Error(`${fieldName} must be provided`);
  }
  
  // Check if valid hex color
  if (!/^#[0-9A-F]{6}$/i.test(color)) {
    throw new Error(`${fieldName} must be a valid hex color code`);
  }
  
  return color.toUpperCase();
};

export const validateThemePreference = (themePreference) => {
  if (!themePreference || typeof themePreference !== 'object' || Array.isArray(themePreference)) {
    throw new Error('themePreference must be an object');
  }
  
  const keys = Object.keys(themePreference);
  if (keys.length !== 2) {
    throw new Error('themePreference must have exactly two properties');
  }
  
  if (!themePreference.backgroundColor || !themePreference.fontColor) {
    throw new Error('themePreference must have backgroundColor and fontColor properties');
  }
  
  const backgroundColor = validateHexColor(themePreference.backgroundColor, 'backgroundColor');
  const fontColor = validateHexColor(themePreference.fontColor, 'fontColor');
  
  if (backgroundColor === fontColor) {
    throw new Error('backgroundColor and fontColor cannot be the same');
  }
  
  return { backgroundColor, fontColor };
};

export const validateRole = (role) => {
  role = validateString(role, 'role');
  role = role.toLowerCase();
  
  if (role !== 'user' && role !== 'superuser') {
    throw new Error('role must be either "user" or "superuser"');
  }
  
  return role;
};

export const getCurrentDate = () => {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const year = now.getFullYear();
  return `${month}/${day}/${year}`;
};

export const getCurrentDateTime = () => {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const year = now.getFullYear();
  
  let hours = now.getHours();
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const ampm = hours >= 12 ? 'PM' : 'AM';
  hours = hours % 12 || 12;
  hours = String(hours).padStart(2, '0');
  
  return `${month}/${day}/${year} ${hours}:${minutes}${ampm}`;
};