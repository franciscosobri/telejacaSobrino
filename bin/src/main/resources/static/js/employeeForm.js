

document.addEventListener("DOMContentLoaded", function () {
	
    const password = document.getElementById("password");
    const repeatedPassword = document.getElementById("repeatedPassword");
	const form = document.getElementById("employeeForm");
	const changePasswordField = document.querySelector(".changePasswordField")
	
    const changePasswordButton = document.getElementById("changePasswordButton");
	const cancelButton = document.getElementById("cancelChangePassword");
	const emptyPasswordField = document.querySelector(".emptyPasswordField");

    changePasswordButton.addEventListener("click", function (e) {
		e.preventDefault(); 
        changePasswordField.style.display = "block";
        cancelButton.style.display="block";
        changePasswordButton.style.display="none";
        emptyPasswordField.style.display="none";

    });
    
    cancelButton.addEventListener("click", function(e){
		e.preventDefault();
		changePasswordField.style.display = "none";
        cancelButton.style.display="none";
        changePasswordButton.style.display="block";
        emptyPasswordField.style.display="block";
        password.value="";
        repeatedPassword.value="";
        const divAlertEmployeeForm = document.getElementById("divAlertEmployeeForm");
        if(divAlertEmployeeForm){
			divAlertEmployeeForm.style.display="none";
		}
	})

    form.addEventListener("submit", function (e) {
		
        const passwordValue = password.value;
        const repeatedPasswordValue = repeatedPassword.value;
		
        if (passwordValue !== repeatedPasswordValue) {
            e.preventDefault();
           	console.log("llega");
            let div = document.createElement("div");
            div.classList="alert alert-danger my-2";
            div.innerText="Las contraseñas deben coincidir.";
            div.id="divAlertEmployeeForm";
            changePasswordField.appendChild(div);
        }
    });
});
