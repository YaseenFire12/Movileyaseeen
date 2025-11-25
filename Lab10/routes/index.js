import authRoutes from './auth_routes.js';

const configRoutes = (app) => {
  app.use('/', authRoutes);

  // 404 handler
  app.use('*', (req, res) => {
    res.status(404).render('error', {
      title: 'Error',
      error: 'Page not found'
    });
  });
};

export default configRoutes;