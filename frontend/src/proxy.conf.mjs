export default [
  {
    context: [
      '/api',
      '/oauth2',
      '/login',
      '/logout'
    ],
    target: 'http://localhost:8080',
    secure: false
  }
];
