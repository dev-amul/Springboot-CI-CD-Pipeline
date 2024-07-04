package com.amul.springboot_cicd.services;

import com.amul.springboot_cicd.dtos.ApiResponseDto;
import com.amul.springboot_cicd.dtos.InventoryRequestDto;
import com.amul.springboot_cicd.modals.Inventory;
import com.amul.springboot_cicd.modals.InventoryStatus;
import com.amul.springboot_cicd.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllInventories(){
        try {
            List<Inventory> inventories = inventoryRepository.findAll();
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(inventories)
                            .message(inventories.size() + " results found!")
                            .build()
            );
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .response("Unable to process right now. Try again later!")
                            .message("Failed to retrieve inventories.")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getInventoryById(String inventoryId) {
        try {
            Optional<Inventory> inventoryOptional = inventoryRepository.findById(inventoryId);
            if (inventoryOptional.isPresent()) {
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(inventoryOptional.get())
                                .message("Inventory found.")
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponseDto.builder()
                                .isSuccess(false)
                                .message("Inventory not found with ID: " + inventoryId)
                                .build()
                );
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .response("Unable to process right now. Try again later!")
                            .message("Failed to retrieve inventory by ID.")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> createInventory(InventoryRequestDto inventoryRequestDto) {
        try {
            InventoryStatus status = inventoryRequestDto.getQuantity() > 0 ? InventoryStatus.In_Stock : InventoryStatus.Out_of_Stock;
            Inventory inventory = Inventory.builder()
                    .itemName(inventoryRequestDto.getItemName())
                    .price(inventoryRequestDto.getPrice())
                    .quantity(inventoryRequestDto.getQuantity())
                    .status(status)
                    .build();
            inventoryRepository.insert(inventory);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Inventory created successfully!")
                            .response(inventory)
                            .build()
            );
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .response("Unable to process right now. Try again later!")
                            .message("Unable to create inventory.")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> editInventory(String inventoryId, InventoryRequestDto inventoryRequestDto) {
        try {
            Optional<Inventory> inventoryOptional = inventoryRepository.findById(inventoryId);
            if (inventoryOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponseDto.builder()
                                .isSuccess(false)
                                .message("Inventory not found with ID: " + inventoryId)
                                .build()
                );
            }
            Inventory inventory = inventoryOptional.get();
            InventoryStatus status = inventoryRequestDto.getQuantity() > 0 ? InventoryStatus.In_Stock : InventoryStatus.Out_of_Stock;
            inventory.setItemName(inventoryRequestDto.getItemName());
            inventory.setPrice(inventoryRequestDto.getPrice());
            inventory.setQuantity(inventoryRequestDto.getQuantity());
            inventory.setStatus(status);
            inventoryRepository.save(inventory);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Inventory updated successfully!")
                            .response(inventory)
                            .build()
            );
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .response("Unable to process right now. Try again later!")
                            .message("Unable to update inventory.")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteInventory(String inventoryId) {
        try {
            if (!inventoryRepository.existsById(inventoryId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponseDto.builder()
                                .isSuccess(false)
                                .message("Inventory not found with ID: " + inventoryId)
                                .build()
                );
            }
            inventoryRepository.deleteById(inventoryId);
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Inventory deleted successfully!")
                            .build()
            );
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .response("Unable to process right now. Try again later!")
                            .message("Unable to delete inventory.")
                            .build()
            );
        }
    }
}