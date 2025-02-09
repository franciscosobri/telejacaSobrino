package com.telejaca.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telejaca.controller.CallId;
import com.telejaca.model.Call;
import com.telejaca.model.CallType;
import com.telejaca.model.Employee;
import com.telejaca.model.User;

public interface CallRepository extends JpaRepository<Call, CallId> {
	
//	@Query(value="SELECT c FROM Call WHERE c.user=:user AND c.employee = :employee AND c.date=:date ")
//	List<Call> callExists();
	
	//SELECT * FROM Llamada ll WHERE ll.empleado_username='sabi_operator' AND ll.usuario_id=2 AND ll.tipo_llamada_id=3 AND ll.fecha='2024-01-20' AND ll.orden = (SELECT max(cll.orden) FROM Llamada cll WHERE cll.empleado_username='sabi_operator' AND cll.usuario_id=2 AND cll.tipo_llamada_id=3 AND cll.fecha='2024-01-20');
	//Call getHigherOrderCall(@Param("usuario_id") Integer userId);
	
	//@Query(value ="SELECT c FROM Call c WHERE c.user=:user AND c.employee=:employee AND c.callType=:callType AND c.date=:date AND c.order = (SELECT max(cll.order) FROM Call cll WHERE c.user=:user AND c.employee=:employee AND c.callType=:callType AND c.date=:date)")
	//Optional<Call> getHigherOrderCall(@Param("user") User user, @Param("employee") Employee employee, @Param("callType") CallType callType, @Param("date") LocalDate date);	
	
	@Query(value = "SELECT * FROM Llamada ll WHERE  ll.usuario_id=:user AND ll.empleado_username=:employee AND ll.tipo_llamada_id=:callType AND ll.fecha=:date AND ll.orden = (SELECT max(cll.orden) FROM Llamada cll WHERE cll.usuario_id=:user AND cll.empleado_username=:employee AND cll.tipo_llamada_id=:callType AND cll.fecha=:date);", nativeQuery=true)
	Optional<Call> getHigherOrderCall(@Param("user") Integer userId, @Param("employee") String employeeUsername, @Param("callType") Integer callTypeId, @Param("date") LocalDate date);	
	
	boolean existsByUserAndEmployeeAndCallTypeAndDate(User user, Employee empl, CallType calltype, LocalDate date);
	
	List<Call> findByUserAndEmployeeAndCallTypeAndDate(User user, Employee empl, CallType callType, LocalDate date);
	
	@Query(value = "SELECT * FROM Llamada ll WHERE ll.usuario_id = :userId " + "AND (ll.fecha, ll.orden) IN ( " + "SELECT MAX(cll.fecha), MAX(cll.orden) FROM Llamada cll " + "WHERE cll.usuario_id = :userId " + "GROUP BY cll.empleado_username);", nativeQuery = true)
	List<Call> findLatestCallsByUser(@Param("userId") Integer userId);

	public List<Call> findByUserAndEmployee(User user, Employee employee);


}
