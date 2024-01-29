module.exports = {
  loginButton: () => {
    return `
        <div class="header-utility-nav__sign-in">
            <a href="http://www-login-local.eaton.com/us/en-us/login.html" class='open-sign-in' target="_self">
                <i class="icon icon-user" aria-hidden="true"></i>
                  <span class='header-utility-nav__signin'>Sign in</span>
                </a>
         </div>
    `;
  }
};

