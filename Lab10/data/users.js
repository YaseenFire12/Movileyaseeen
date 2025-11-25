//import mongo collections, bcrypt and implement the following data functions
import {users} from '../config/mongoCollections.js';
import bcrypt from 'bcrypt';
import {
  validateName,
  validateUserId,
  validatePassword,
  validateQuote,
  validateThemePreference,
  validateRole,
  getCurrentDate,
  getCurrentDateTime
} from '../helpers.js';

const SALT_ROUNDS = 16;

export const register = async (
  firstName,
  lastName,
  userId,
  password,
  favoriteQuote,
  themePreference,
  role
) => {
  // Validate all inputs
  firstName = validateName(firstName, 'firstName');
  lastName = validateName(lastName, 'lastName');
  userId = validateUserId(userId); // Returns lowercase
  password = validatePassword(password);
  favoriteQuote = validateQuote(favoriteQuote);
  themePreference = validateThemePreference(themePreference);
  role = validateRole(role); // Returns lowercase
  
  // Check if userId already exists (case-insensitive)
  const usersCollection = await users();
  const existingUser = await usersCollection.findOne({
    userId: userId
  });
  
  if (existingUser) {
    throw new Error('A user with that userId already exists');
  }
  
  // Hash the password
  const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS);
  
  // Get signup date
  const signupDate = getCurrentDate();
  
  // Create user object
  const newUser = {
    firstName,
    lastName,
    userId,
    password: hashedPassword,
    favoriteQuote,
    themePreference,
    role,
    signupDate,
    lastLogin: null
  };
  
  // Insert into database
  const insertInfo = await usersCollection.insertOne(newUser);
  
  if (!insertInfo.acknowledged || !insertInfo.insertedId) {
    throw new Error('Could not add user');
  }
  
  return {registrationCompleted: true};
};

export const login = async (userId, password) => {
  // Validate inputs
  userId = validateUserId(userId); // Returns lowercase
  password = validatePassword(password);
  
  // Find user (case-insensitive)
  const usersCollection = await users();
  const user = await usersCollection.findOne({
    userId: userId
  });
  
  if (!user) {
    throw new Error('Either the userId or password is invalid');
  }
  
  // Compare passwords
  const passwordMatch = await bcrypt.compare(password, user.password);
  
  if (!passwordMatch) {
    throw new Error('Either the userId or password is invalid');
  }
  
  // Update lastLogin
  const lastLogin = getCurrentDateTime();
  
  await usersCollection.updateOne(
    {_id: user._id},
    {$set: {lastLogin: lastLogin}}
  );
  
  // Return user data (without password)
  return {
    firstName: user.firstName,
    lastName: user.lastName,
    userId: user.userId,
    favoriteQuote: user.favoriteQuote,
    themePreference: user.themePreference,
    role: user.role,
    signupDate: user.signupDate,
    lastLogin: lastLogin
  };
};