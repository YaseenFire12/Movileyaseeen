// Client-side form validation

(function() {
  // Helper validation functions
  const validateName = (name, fieldName) => {
    if (!name || name.trim().length === 0) {
      throw new Error(`${fieldName} cannot be empty`);
    }
    name = name.trim();
    if (!/^[a-zA-Z]+$/.test(name)) {
      throw new Error(`${fieldName} must contain only letters`);
    }
    if (name.length < 2 || name.length > 20) {
      throw new Error(`${fieldName} must be between 2 and 20 characters`);
    }
    return name;
  };

  const validateUserId = (userId) => {
    if (!userId || userId.trim().length === 0) {
      throw new Error('userId cannot be empty');
    }
    userId = userId.trim();
    if (!/^[a-zA-Z0-9]+$/.test(userId)) {
      throw new Error('userId must contain only letters and numbers');
    }
    if (userId.length < 5 || userId.length > 10) {
      throw new Error('userId must be between 5 and 10 characters');
    }
    return userId;
  };

  const validatePassword = (password) => {
    if (!password || password.length === 0) {
      throw new Error('Password cannot be empty');
    }
    if (password.includes(' ')) {
      throw new Error('Password cannot contain spaces');
    }
    if (password.length < 8) {
      throw new Error('Password must be at least 8 characters long');
    }
    if (!/[A-Z]/.test(password)) {
      throw new Error('Password must contain at least one uppercase letter');
    }
    if (!/[0-9]/.test(password)) {
      throw new Error('Password must contain at least one number');
    }
    if (!/[^a-zA-Z0-9]/.test(password)) {
      throw new Error('Password must contain at least one special character');
    }
    return password;
  };

  const validateQuote = (quote) => {
    if (!quote || quote.trim().length === 0) {
      throw new Error('Favorite quote cannot be empty');
    }
    quote = quote.trim();
    if (quote.length < 20 || quote.length > 255) {
      throw new Error('Favorite quote must be between 20 and 255 characters');
    }
    return quote;
  };

  const validateHexColor = (color, fieldName) => {
    if (!color) {
      throw new Error(`${fieldName} must be provided`);
    }
    if (!/^#[0-9A-F]{6}$/i.test(color)) {
      throw new Error(`${fieldName} must be a valid hex color code`);
    }
    return color;
  };

  const validateRole = (role) => {
    if (!role || role.trim().length === 0) {
      throw new Error('Role must be selected');
    }
    role = role.toLowerCase();
    if (role !== 'user' && role !== 'superuser') {
      throw new Error('Role must be either "user" or "superuser"');
    }
    return role;
  };

  const showError = (message) => {
    // Try to find existing error element
    let errorElement = document.querySelector('.client-error');
    
    if (!errorElement) {
      errorElement = document.createElement('p');
      errorElement.className = 'error client-error';
      // Insert at the top of the page
      const firstElement = document.body.firstChild;
      document.body.insertBefore(errorElement, firstElement);
    }
    
    errorElement.textContent = message;
    errorElement.scrollIntoView({ behavior: 'smooth' });
  };

  const clearError = () => {
    const errorElement = document.querySelector('.client-error');
    if (errorElement) {
      errorElement.remove();
    }
  };

  // Register form validation
  const registerForm = document.getElementById('signup-form');
  if (registerForm) {
    registerForm.addEventListener('submit', function(event) {
      event.preventDefault();
      clearError();

      try {
        // Get all form values
        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;
        const userId = document.getElementById('userId').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const favoriteQuote = document.getElementById('favoriteQuote').value;
        const backgroundColor = document.getElementById('backgroundColor').value;
        const fontColor = document.getElementById('fontColor').value;
        const role = document.getElementById('role').value;

        // Validate all fields
        validateName(firstName, 'First name');
        validateName(lastName, 'Last name');
        validateUserId(userId);
        validatePassword(password);
        
        // Check passwords match
        if (password !== confirmPassword) {
          throw new Error('Passwords do not match');
        }
        
        validateQuote(favoriteQuote);
        validateHexColor(backgroundColor, 'Background color');
        validateHexColor(fontColor, 'Font color');
        
        // Check colors are different
        if (backgroundColor.toUpperCase() === fontColor.toUpperCase()) {
          throw new Error('Background color and font color cannot be the same');
        }
        
        validateRole(role);

        // If all validation passes, submit the form
        this.submit();
        
      } catch (error) {
        showError(error.message);
      }
    });
  }

  // Login form validation
  const loginForm = document.getElementById('signin-form');
  if (loginForm) {
    loginForm.addEventListener('submit', function(event) {
      event.preventDefault();
      clearError();

      try {
        // Get form values
        const userId = document.getElementById('userId').value;
        const password = document.getElementById('password').value;

        // Validate fields
        validateUserId(userId);
        validatePassword(password);

        // If all validation passes, submit the form
        this.submit();
        
      } catch (error) {
        showError(error.message);
      }
    });
  }
})();