package main.ErrandData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrandDataRepository extends JpaRepository<ErrandData, Long> {
}
