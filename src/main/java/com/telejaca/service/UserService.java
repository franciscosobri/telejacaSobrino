package com.telejaca.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	/** This function returns all the users active in the system, or it will throw an UserException error if no data was found. */
	public List<User> getAllActiveUsers() throws UserException{
		List<User> users =  userRepository.getAllActiveUsers();
		if(users.isEmpty()) {
			throw new UserException("No hay usuarios activos registrados.");
		}
		return users;
	}
	
	public List<User> getAllActiveUsersOrderedBySurname() throws UserException{
		List<User> users =  userRepository.getAllActiveUsersOrderBySurname();
		if(users.isEmpty()) {
			throw new UserException("No hay usuarios activos registrados.");
		}
		return users;
	}
	
//	Intento de paginación
	public Page<User> getAllActiveUsers(Optional<Integer> pageNum, Optional<Integer> pageSize) {
	    int realPageNum = pageNum.orElse(0);
	    int realPageSize = pageSize.orElse(10);

	    Pageable pageable = PageRequest.of(realPageNum, realPageSize, Sort.by("name").ascending());

	    return userRepository.findAllByCancellationCauseIsNullAndCancellationDateIsNull(pageable);
	}

	
	public List<User> searchUserByName(String name){
		return userRepository.findByNameLike(name);
	}
	
	public List<User> searchUserBySurname(String surname){
		return userRepository.findBySurnameLike(surname);
	}
	
	/** This function returns the user if it is valid (it exists and is not unsubscribe), it will throw an NoSuchElementException if it was not find or an UserException if he/she is unsbuscribe. 
	 * @throws UserException
	 * @Throws NoSuchElementException */
	public Optional<User> findUserById(Integer Id) throws UserException {
		Optional<User> user = userRepository.findById(Id);
		User userValid = user.orElseThrow();
		if (userValid.getCancellationCause()!=null) {
			throw new UserException("El usuario que busca no está activo.");
		}
		return user;
	}
	
	/**This method receives a String with the value of a search and it will find all users
	 * whose name, surname or phone matches the input value.
	 * @throws UserException */
	public List<User> searchActiveUser(String searchValue) throws UserException{
		List<User> userList = new ArrayList<User>();
		if(searchValue==null || searchValue.isBlank()) {
			userList.addAll(getAllActiveUsers());
		}else {
			userList=userRepository.getUsersLike(searchValue);
			if(userList.isEmpty()) { 
				throw new UserException("No hay usuarios activos que coincidan con esta búsqueda.");
			}
		}
		return userList;		
	}
	
	public Page<User> searchActiveUser(String searchValue, Optional<Integer> page, Optional<Integer> size) {
	    Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
	    return userRepository.searchUsers(searchValue, pageable);
	}
	
	/**This function change a String id into an Integer value and returns it. 
	 * If the id is not convertible, it will send an UserException error.
	 * @param String id
	 * @return Integer id
	 * @throws NumberFormatException */
	public Integer idIntoInt(String id) throws NumberFormatException {
		Integer idInt = -1;
		try {
			idInt = Integer.parseInt(id);
		}catch(NumberFormatException nfe) {
			throw new NumberFormatException("Id no numérica.");
		}
		return idInt;
	}
	
	/** This function adds a new user into the database or throws an exception.
	 * @param User (new user to add)
	 * @return Integer (new user Id)
	 * @throws UserException
	 *  */
	public Integer addUser(User u) throws UserException {
		try {
			/*Validamos que, si no se ha seleccionado un género, se guarde como nulo, y no como N,que dará fallo en la base de datos. */
			u = this.setValidGender(u);
			/* Persistimos cambios */
			User newUser = userRepository.saveAndFlush(u);	
			
			return newUser.getId();
		}catch(IllegalArgumentException ie) {
			throw new UserException("Error al añadir al usuario. Usuario nulo. "+ie.getMessage());
		}catch(OptimisticLockingFailureException olfe) {
			throw new UserException(olfe.getMessage());
		}catch(Exception e) {
			throw new UserException("Error inesperado "+e.getMessage());
		}
	}
	
	public User editUser (User userEdited) throws UserException {
		try {			
			/*Validamos que, si no se ha seleccionado un género, se guarde como nulo, y no como N,que dará fallo en la base de datos. */
			userEdited = this.setValidGender(userEdited);
			
			User userChanged = userRepository.save(userEdited);
			return userChanged;
		}catch(Exception e) {
			throw new UserException("Error al editar el usuario: "+e.getMessage());
		}
	}
	
	public void unsubscribeUser (User userToUnsb) throws UserException {
		
		try {
			/* Nos aseguramos que el género no dará problema si no fue asignado */
			userToUnsb = this.setValidGender(userToUnsb);
			
			/* Asignamos la fecha de cancelación */
			userToUnsb.setCancellationDate(LocalDate.now());	
			
			/* Persistimos cambios en BBDD */
			this.userRepository.save(userToUnsb);
			
		}catch(Exception e) {
			throw new UserException("El usuario no ha podido ser eliminado. "+e.getMessage());
		}
	}
	
	/** This function allows to save a null gender. */
	public User setValidGender (User user) {
		if(user.getGender().equals('N')) {
			user.setGender(null);
		}
		return user;
	}
	
	/**This function will return the user if it is valid or an UserException otherwise*/
	public User getUserValid(String id) throws UserException {
		/* Validations */
		User userValid = null;

		if(id.isBlank()) {
			throw new UserException("Id de usuario vacía.");
		}else {
			try {
				Integer idInt = this.idIntoInt(id);
				Optional<User> user = this.findUserById(idInt);
				if(user.orElse(null)==null) {
					throw new UserException("Usuario no encontrado.");
				}else {
					userValid= user.get();
				}
			}catch (NoSuchElementException nse) {
				throw new UserException("Usuario no encontrado.");
			}catch(UserException ue) {
				throw ue;
			}catch(NumberFormatException nfe) {
				throw nfe;
			}
		}
		
		return userValid;
		
	}

	public Page<User> searchActiveUser(String searchValue, Optional<Integer> page, Optional<Integer> size, String sortField, String sortDirection) {
	    // Dirección de la ordenación
	    Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

	    // Objeto Pageable con la ordenación incluida
	    Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);

	    // Resultados filtrados y ordenados
	    return userRepository.searchUsers(searchValue, pageable);
	}

	
	
	

}
