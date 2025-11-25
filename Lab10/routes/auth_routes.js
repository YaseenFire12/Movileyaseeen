import {Router} from 'express';
import * as userData from '../data/users.js';
import * as helpers from '../helpers.js';
import {
  loginGetMiddleware,
  registerGetMiddleware,
  userMiddleware,
  superuserMiddleware,
  signoutMiddleware
} from '../middleware.js';

const router = Router();

router.route('/').get(async (req, res) => {
  const isAuthenticated = req.session && req.session.user;
  const user = req.session ? req.session.user : null;
  
  res.render('home', {
    title: 'Lab 10 - Login System',
    isAuthenticated,
    user,
    applyTheme: isAuthenticated,
    themePreference: user ? user.themePreference : null
  });
});
router.route('/register')
  .get(registerGetMiddleware, async (req, res) => {
    res.render('register', {
      title: 'Register',
      applyTheme: false
    });
  })
  .post(async (req, res) => {
    // Get all form data
    let {firstName, lastName, userId, password, confirmPassword, favoriteQuote, backgroundColor, fontColor, role} = req.body;
    
    // Trim all string inputs
    if (firstName) firstName = firstName.trim();
    if (lastName) lastName = lastName.trim();
    if (userId) userId = userId.trim();
    if (favoriteQuote) favoriteQuote = favoriteQuote.trim();
    
    try {
      // Check all fields are provided
      if (!firstName || !lastName || !userId || !password || !confirmPassword || !favoriteQuote || !backgroundColor || !fontColor || !role) {
        throw new Error('All fields must be provided');
      }
      
      // Validate firstName
      firstName = helpers.validateName(firstName, 'firstName');
      
      // Validate lastName
      lastName = helpers.validateName(lastName, 'lastName');
      
      // Validate userId
      userId = helpers.validateUserId(userId);
      
      // Validate password
      password = helpers.validatePassword(password);
      
      // Check passwords match
      if (password !== confirmPassword) {
        throw new Error('Passwords do not match');
      }
      
      // Validate quote
      favoriteQuote = helpers.validateQuote(favoriteQuote);
      
      // Validate colors
      backgroundColor = helpers.validateHexColor(backgroundColor, 'backgroundColor');
      fontColor = helpers.validateHexColor(fontColor, 'fontColor');
      
      // Check colors are different
      if (backgroundColor === fontColor) {
        throw new Error('backgroundColor and fontColor cannot be the same');
      }
      
      // Validate role
      role = helpers.validateRole(role);
      
      // Create themePreference object
      const themePreference = {backgroundColor, fontColor};
      
      // Call register function
      const result = await userData.register(
        firstName,
        lastName,
        userId,
        password,
        favoriteQuote,
        themePreference,
        role
      );
      
      if (result.registrationCompleted) {
        return res.redirect('/login');
      } else {
        throw new Error('Internal Server Error');
      }
      
    } catch (error) {
      return res.status(400).render('register', {
        title: 'Register',
        error: error.message,
        firstName,
        lastName,
        userId,
        favoriteQuote,
        backgroundColor,
        fontColor,
        role,
        applyTheme: false
      });
    }
  });
router
  .route('/login')
  .get(loginGetMiddleware, async (req, res) => {
    res.render('login', {
      title: 'Login',
      applyTheme: false
    });
  })
  .post(async (req, res) => {
    let {userId, password} = req.body;
    
    // Trim inputs
    if (userId) userId = userId.trim();
    
    try {
      // Check fields are provided
      if (!userId || !password) {
        throw new Error('Both userId and password must be provided');
      }
      
      // Validate userId
      userId = helpers.validateUserId(userId);
      
      // Validate password
      password = helpers.validatePassword(password);
      
      // Call login function
      const user = await userData.login(userId, password);
      
      // Store user in session
      req.session.user = user;
      
      // Redirect based on role
      if (user.role === 'superuser') {
        return res.redirect('/superuser');
      } else {
        return res.redirect('/user');
      }
      
    } catch (error) {
      return res.status(400).render('login', {
        title: 'Login',
        error: error.message,
        userId,
        applyTheme: false
      });
    }
  });

router.route('/user').get(userMiddleware, async (req, res) => {
  const user = req.session.user;
  
  res.render('user', {
    title: 'User Profile',
    user,
    currentTime: helpers.getCurrentDateTime().split(' ')[1],
    currentDate: helpers.getCurrentDate(),
    applyTheme: true,
    themePreference: user.themePreference
  });
});

router.route('/superuser').get(superuserMiddleware, async (req, res) => {
  const user = req.session.user;
  
  res.render('superuser', {
    title: 'Super User',
    user,
    currentTime: helpers.getCurrentDateTime().split(' ')[1],
    currentDate: helpers.getCurrentDate(),
    applyTheme: true,
    themePreference: user.themePreference
  });
});

router.route('/signout').get(signoutMiddleware, async (req, res) => {
  // Destroy session
  req.session.destroy(() => {
    res.render('signout', {
      title: 'Signed Out',
      applyTheme: false
    });
  });
});