package com.telejaca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telejaca.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Override
	Optional<User> findById(Integer id);
	
	List<User> findByNameLike(String name);
	public Page<User> findByNameLike(String keyword, Pageable pageable);
	
	List <User> findBySurnameLike(String surname);
	public Page<User> findBySurnameLike(String keyword, Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE cancellationCause IS NULL AND cancellationDate IS NULL")
	List<User> getAllActiveUsers();
	
	@Query("SELECT u FROM User u WHERE cancellationCause IS NULL AND cancellationDate IS NULL ORDER BY u.surname")
	List<User> getAllActiveUsersOrderBySurname();
	
	@Query(value= "SELECT u FROM User u JOIN Phone ph ON u.id=ph.phoneUser.id WHERE (UPPER(u.name) LIKE CONCAT('%', UPPER(:searchValue), '%') OR UPPER(u.surname) LIKE CONCAT('%', UPPER(:searchValue), '%') OR ph.number LIKE CONCAT('%', UPPER(:searchValue), '%')) AND u.cancellationCause IS NULL")
	List<User> getUsersLike(@Param("searchValue") String searchValue);

	Page<User> findAllByCancellationCauseIsNullAndCancellationDateIsNull(Pageable pageable);

	//	Query para la búsqueda
	@Query("SELECT u FROM User u " +
		       "LEFT JOIN u.phoneList p " +
		       "WHERE (u.name LIKE %:searchText% OR " +
		       "u.surname LIKE %:searchText% OR " +
		       "p.number LIKE %:searchText%) " +
		       "AND u.cancellationCause IS NULL " +
		       "AND u.cancellationDate IS NULL")
		Page<User> searchUsers(String searchText, Pageable pageable);
	
	/*Query nativa, desactivada porque coincidía el mismo nombre "id" para dos tablas, y había que seleccionar todos los campos uno a uno*/
	/*	@Query(value= "SELECT * FROM Usuarios u JOIN Telefono tlf ON u.id=tlf.usuario_id WHERE (UPPER(u.nombre) LIKE UPPER('%:searchValue%') OR UPPER(u.apellido) LIKE UPPER('%:searchValue%') OR tlf.telefono LIKE '%:searchValue%') AND u.causa_baja IS NULL; ", nativeQuery= true)
*/
}
