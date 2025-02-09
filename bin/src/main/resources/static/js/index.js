
const images = ['/img/operator0.png', '/img/operator1.png'];

let randomNumber = Math.floor(Math.random() * 2);

const indexImg = document.getElementById("indexImg");

document.addEventListener("DOMContentLoaded", ()=>{
	if(indexImg){
	    indexImg.src = images[randomNumber];	
	}
})


// Script para botón de limpiar búsqueda
const clearButton = document.getElementById('clearButton');
const searchInput = document.getElementById('searchInput');
const searchForm = document.getElementById('searchForm');

clearButton.addEventListener('click', () => {
    searchInput.value = '';
    searchForm.submit();
    });