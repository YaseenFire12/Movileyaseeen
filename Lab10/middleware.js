/*
You can choose to define all your middleware functions here, 
export them and then import them into your app.js and attach them that that.
add.use(myMiddleWare()). you can also just define them in the app.js if you like as seen in lecture 10's lecture code example. If you choose to write them in the app.js, you do not have to use this file. 
*/

export const loggingMiddleware = (req, res, next) => {
  const timestamp = new Date().toUTCString();
  const method = req.method;
  const route = req.path;
  
  let authStatus;
  if (req.session && req.session.user) {
    const role = req.session.user.role === 'superuser' ? 'Super User' : 'User';
    authStatus = `Authenticated ${role}`;
  } else {
    authStatus = 'Non-Authenticated';
  }
  
  console.log(`[${timestamp}]: ${method} ${route} (${authStatus})`);
  next();
};

export const loginGetMiddleware = (req, res, next) => {
  if (req.session && req.session.user) {
    if (req.session.user.role === 'superuser') {
      return res.redirect('/superuser');
    } else {
      return res.redirect('/user');
    }
  }
  next();
};

export const registerGetMiddleware = (req, res, next) => {
  if (req.session && req.session.user) {
    if (req.session.user.role === 'superuser') {
      return res.redirect('/superuser');
    } else {
      return res.redirect('/user');
    }
  }
  next();
};

export const userMiddleware = (req, res, next) => {
  if (!req.session || !req.session.user) {
    return res.redirect('/login');
  }
  next();
};

export const superuserMiddleware = (req, res, next) => {
  if (!req.session || !req.session.user) {
    return res.redirect('/login');
  }
  
  if (req.session.user.role !== 'superuser') {
    return res.status(403).render('error', {
      title: 'Error',
      error: 'You do not have permission to view this page',
      showUserLink: true
    });
  }
  
  next();
};

export const signoutMiddleware = (req, res, next) => {
  if (!req.session || !req.session.user) {
    return res.redirect('/login');
  }
  next();
};