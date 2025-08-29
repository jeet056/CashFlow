package services.cashflow.statistics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import services.cashflow.statistics.domain.timeseries.DataPoint;
import services.cashflow.statistics.domain.timeseries.DataPointId;

import java.util.List;

@Repository
public interface DataPointRepository extends MongoRepository<DataPoint, DataPointId> {

	List<DataPoint> findByIdAccount(String account);
	DataPoint save(DataPoint point);
}
