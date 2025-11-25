import express from 'express';
import exphbs from 'express-handlebars';
import session from 'express-session';
import configRoutes from './routes/index.js';
import {loggingMiddleware} from './middleware.js';
import {getCurrentDateTime, getCurrentDate} from './helpers.js';

const app = express();
const PORT = 3000;

// Configure Handlebars with helpers
app.engine('handlebars', exphbs.engine({
  defaultLayout: 'main',
  helpers: {
    // Helper to format current date and time
    getCurrentDateTime: () => getCurrentDateTime(),
    getCurrentDate: () => getCurrentDate(),
    // Helper for equality comparison
    eq: (a, b) => a === b
  }
}));
app.set('view engine', 'handlebars');

// Middleware
app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use('/public', express.static('public'));

// Session middleware - MUST be named AuthenticationState
app.use(session({
  name: 'AuthenticationState',
  secret: 'some secret string!',
  resave: false,
  saveUninitialized: false,
  cookie: {maxAge: 60000 * 60} // 1 hour
}));

// Apply logging middleware to all routes
app.use(loggingMiddleware);

// Configure routes
configRoutes(app);

// Start server
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});