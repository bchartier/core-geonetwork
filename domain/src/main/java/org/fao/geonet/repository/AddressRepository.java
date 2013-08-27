package org.fao.geonet.repository;

import org.fao.geonet.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Data Access object for accessing {@link org.fao.geonet.domain.MetadataCategory} entities.
 *
 * @author Jesse
 */
public interface AddressRepository extends JpaRepository<Address, Integer>, JpaSpecificationExecutor<Address> {
    /**
     * Find all the addresses in the given zip code.
     *
     * @param zip the zip code
     * @return all the addresses in the given zip code
     */
    public List<Address> findAllByZip(String zip);
}
