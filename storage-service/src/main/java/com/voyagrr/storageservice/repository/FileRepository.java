package com.voyagrr.storageservice.repository;

import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByDirectory(Directory directory);

}
