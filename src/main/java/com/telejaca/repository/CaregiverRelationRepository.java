package com.telejaca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telejaca.model.CaregiverRelation;
import com.telejaca.model.CaregiverRelationId;
import com.telejaca.model.User;

public interface CaregiverRelationRepository extends JpaRepository<CaregiverRelation, CaregiverRelationId> {

	//@Query(value="SELECT * FROM Usuarios u JOIN Es_Cuidador c ON u.id= c.cuidador_id WHERE u.id=:id; ", nativeQuery = true)
	//@Query(value="SELECT u FROM User u JOIN CaregiverRelation c ON u.id=c.recipentUser WHERE u.id=:id;")
	@Query(value="SELECT c.recipentUser FROM CaregiverRelation c WHERE c.caregiver=:id")
	List<User> getRecipentUsersByCarerId(@Param("id") Integer id);
	
	//@Query(value="SELECT c.recipentUser FROM CaregiverRelation c WHERE c.caregiver.getId()=:id")
	
	//@Query(value="SELECT c.caregiver FROM CaregiverRelation c WHERE c.recipentUser=:id")
	//@Query(value="SELECT u FROM User u JOIN CaregiverRelation c ON u=c.caregiver WHERE c.recipentUser.id=:id")
	@Query(value="SELECT c.caregiver FROM CaregiverRelation c WHERE c.recipentUser.id = :id")
	List<User> getCaregiversListByRecipentUserId(@Param("id") Integer id);
	
	@Query(value="SELECT count(cr)>0 FROM CaregiverRelation cr WHERE cr.recipentUser.id = :recipentId AND cr.caregiver.id = :caregiverId")
	boolean relationExists(@Param("caregiverId") Integer caregiverId, @Param("recipentId") Integer recipentId);
	
	
}
