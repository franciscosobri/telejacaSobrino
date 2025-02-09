document.addEventListener('DOMContentLoaded', function () {

  const mobileLogo = document.getElementById('mobile-logo');
  const userLogin = document.getElementById('user-login');
  
  const navbarCollapse = document.getElementById('navbarNav');

  navbarCollapse.addEventListener('show.bs.collapse', function () {
    mobileLogo.classList.add('expanded-logo'); // Agrega la clase al logo
    userLogin.classList.add('expanded-login');
  });

  navbarCollapse.addEventListener('hide.bs.collapse', function () {
    mobileLogo.classList.remove('expanded-logo'); // Elimina la clase del logo
    userLogin.classList.remove('expanded-login');
  });
});