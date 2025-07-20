package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.livinggoodsbackend.livinggoodsbackend.Model.ChaChpMapping;
import com.livinggoodsbackend.livinggoodsbackend.Model.ChaCuMapping;
import com.livinggoodsbackend.livinggoodsbackend.Model.ChpCuMapping;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChaChpMappingRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRecordRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityUnitRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaCuMappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaCuMappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaDashboardResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaDashboardStatsDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpBasicInfoDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpCuMappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpCuMappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpDashboardDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpDashboardStatsDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityRecordDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CuBasicInfoDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.MappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.MappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.UserResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpDashboardStatsDTO;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChaCuMappingRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChpCuMappingRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommodityRecordRepository commodityRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChaChpMappingRepository mappingRepository;

    @Value("${app.reset.token.expiry:3600000}") // 1 hour in milliseconds
    private long resetTokenExpiryMs;

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private ChaCuMappingRepository chaCuMappingRepository;

    @Autowired
    private ChpCuMappingRepository chpCuMappingRepository;

    @Autowired
    private CommodityUnitRepository communityUnitRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        // Validation
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Set default/required values
        user.setId(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(null);
        user.setVersion(0L);

        // Set default role if not provided
        // if (user.getRole() == null) {
        // user.setRole(Role.USER);
        // }

        // Save and return
        return userRepository.save(user);
    }


    public ChaCuMappingResponseDTO mapChaToCu(ChaCuMappingRequestDTO request) {
    ChaCuMapping mapping = new ChaCuMapping();
    mapping.setChaId(request.getChaId());
    mapping.setCommunityUnitId(request.getCommunityUnitId());

    ChaCuMapping saved = chaCuMappingRepository.save(mapping);

    ChaCuMappingResponseDTO response = new ChaCuMappingResponseDTO();
    response.setId(saved.getId());
    response.setChaId(saved.getChaId());
    response.setCommunityUnitId(saved.getCommunityUnitId());

    return response;
}
public ChpCuMappingResponseDTO mapChpToCu(ChpCuMappingRequestDTO request) {
    ChpCuMapping mapping = new ChpCuMapping();
    mapping.setChpId(request.getChpId());
    mapping.setCommunityUnitId(request.getCommunityUnitId());

    ChpCuMapping saved = chpCuMappingRepository.save(mapping);

    ChpCuMappingResponseDTO response = new ChpCuMappingResponseDTO();
    response.setId(saved.getId());
    response.setChpId(saved.getChpId());
    response.setCommunityUnitId(saved.getCommunityUnitId());

    return response;
}


// Get all CHP IDs mapped to a Community Unit
   public List<ChpBasicInfoDTO> getChpDetailsByCommunityUnit(Long communityUnitId) {
    List<ChpCuMapping> mappings = chpCuMappingRepository.findByCommunityUnitId(communityUnitId);

    return mappings.stream()
            .map(mapping -> userRepository.findById(mapping.getChpId()).orElse(null))
            .filter(user -> user != null)
            .map(user -> {
                ChpBasicInfoDTO dto = new ChpBasicInfoDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setPhoneNumber(user.getPhoneNumber());
                return dto;
            })
            .collect(Collectors.toList());
}
        public List<CuBasicInfoDTO> getCommunityUnitDetailsByCha(Long chaId) {
            List<ChaCuMapping> mappings = chaCuMappingRepository.findByChaId(chaId);

            return mappings.stream()
                    .map(mapping -> communityUnitRepository.findById(mapping.getCommunityUnitId()).orElse(null))
                    .filter(cu -> cu != null)
                    .map(cu -> {
                        CuBasicInfoDTO dto = new CuBasicInfoDTO();
                        dto.setId(cu.getId());
                        dto.setName(cu.getCommunityUnitName());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }


    // Get all Community Unit IDs mapped to a CHA
    public List<Long> getCusByCha(Long chaId) {
        List<ChaCuMapping> mappings = chaCuMappingRepository.findByChaId(chaId);
        return mappings.stream().map(ChaCuMapping::getCommunityUnitId).collect(Collectors.toList());
    }


    public List<ChpBasicInfoDTO> getChpsByCha(Long chaId) {
    // Step 1: Get all CU IDs assigned to the CHA
    List<Long> cuIds = chaCuMappingRepository.findByChaId(chaId).stream()
            .map(ChaCuMapping::getCommunityUnitId)
            .collect(Collectors.toList());

    if (cuIds.isEmpty()) return new ArrayList<>();

    // Step 2: Get all CHPs mapped to those CUs
    List<Long> chpIds = chpCuMappingRepository.findByCommunityUnitIdIn(cuIds).stream()
            .map(ChpCuMapping::getChpId)
            .distinct()
            .collect(Collectors.toList());

    if (chpIds.isEmpty()) return new ArrayList<>();

    // Step 3: Get full user details for those CHPs
    return userRepository.findAllById(chpIds).stream()
            .map(user -> {
                ChpBasicInfoDTO dto = new ChpBasicInfoDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setPhoneNumber(user.getPhoneNumber());
                return dto;
            })
            .collect(Collectors.toList());
}
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate random token
        String token = UUID.randomUUID().toString();

        // Set token and expiry
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpiryMs));
        userRepository.save(user);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n\n" +
                "http://your-frontend-url/reset-password?token=" + token);

        emailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Update password and clear reset token
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    public List<UserResponseDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream().map(user -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            // dto.setRole(user.getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }


public ChaDashboardResponseDTO getCHPsByCHA(Long chaId) {
    // STEP 1: Get CU IDs mapped to this CHA
    List<Long> cuIds = chaCuMappingRepository.findByChaId(chaId).stream()
        .map(ChaCuMapping::getCommunityUnitId)
        .distinct()
        .collect(Collectors.toList());

    if (cuIds.isEmpty()) return new ChaDashboardResponseDTO();

    // STEP 2: Get all CHP IDs mapped to those CU IDs
    List<Long> chpIds = chpCuMappingRepository.findByCommunityUnitIdIn(cuIds).stream()
        .map(ChpCuMapping::getChpId)
        .distinct()
        .collect(Collectors.toList());

    if (chpIds.isEmpty()) return new ChaDashboardResponseDTO();

    // Now delegate to the existing logic you had:
    return computeChaDashboardFromChpIds(chpIds);
}



private ChaDashboardResponseDTO computeChaDashboardFromChpIds(List<Long> chpIds) {
    YearMonth currentMonth = YearMonth.now();
    LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
    LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

    List<ChpDashboardDTO> chpDtos = new ArrayList<>();

    for (User user : userRepository.findAllById(chpIds)) {
        ChpDashboardDTO dto = new ChpDashboardDTO();
        dto.setChpId(user.getId());
        dto.setChpUsername(user.getUsername());
        dto.setChpEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        List<CommodityRecordDTO> records = commodityRecordRepository.findByChp_Id(user.getId()).stream()
            .map(record -> {
                CommodityRecordDTO recDto = new CommodityRecordDTO();
                recDto.setId(record.getId());
                recDto.setStockOnHand(record.getStockOnHand());
                recDto.setQuantityIssued(record.getQuantityIssued());
                recDto.setClosingBalance(record.getClosingBalance());
                recDto.setLastRestockDate(record.getLastRestockDate());
                recDto.setQuantityConsumed(record.getQuantityConsumed());
                recDto.setQuantityExpired(record.getQuantityExpired());
                recDto.setQuantityDamaged(record.getQuantityDamaged());
                recDto.setQuantityToOrder(record.getQuantityToOrder());
                recDto.setEarliestExpiryDate(record.getEarliestExpiryDate());
                recDto.setRecordDate(record.getRecordDate());
                recDto.setExcessQuantityReturned(record.getExcessQuantityReturned());
                recDto.setConsumptionPeriod(record.getConsumptionPeriod());
                recDto.setStockOutDate(record.getStockOutDate());

                if (record.getCommodity() != null) {
                    recDto.setCommodityId(record.getCommodity().getId());
                    recDto.setCommodityName(record.getCommodity().getName());
                }

                if (record.getCommunityUnit() != null) {
                    recDto.setCommunityUnitId(record.getCommunityUnit().getId());
                    recDto.setCommunityUnitName(record.getCommunityUnit().getCommunityUnitName());

                    if (record.getCommunityUnit().getLinkFacility() != null) {
                        recDto.setFacilityId(record.getCommunityUnit().getLinkFacility().getId());
                        recDto.setFacilityName(record.getCommunityUnit().getLinkFacility().getName());
                    }

                    if (record.getCommunityUnit().getWard() != null) {
                        recDto.setWardId(record.getCommunityUnit().getWard().getId());
                        recDto.setWardName(record.getCommunityUnit().getWard().getName());

                        if (record.getCommunityUnit().getWard().getSubCounty() != null) {
                            recDto.setSubCountyId(record.getCommunityUnit().getWard().getSubCounty().getId());
                            recDto.setSubCountyName(record.getCommunityUnit().getWard().getSubCounty().getName());

                            if (record.getCommunityUnit().getWard().getSubCounty().getCounty() != null) {
                                recDto.setCountyId(record.getCommunityUnit().getWard().getSubCounty().getCounty().getId());
                                recDto.setCountyName(record.getCommunityUnit().getWard().getSubCounty().getCounty().getName());
                            }
                        }
                    }
                }

                return recDto;
            }).collect(Collectors.toList());

        dto.setCommodityRecords(records);

        ChpDashboardStatsDTO stats = new ChpDashboardStatsDTO();
        stats.setTotalRecords(records.size());
        stats.setTotalIssued(records.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityIssued()).orElse(0)).sum());
        stats.setTotalConsumed(records.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityConsumed()).orElse(0)).sum());
        stats.setTotalExpired(records.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityExpired()).orElse(0)).sum());
        stats.setTotalDamaged(records.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityDamaged()).orElse(0)).sum());

        List<String> outOfStock = records.stream().filter(r -> r.getStockOnHand() != null && r.getStockOnHand() < 1)
            .map(CommodityRecordDTO::getCommodityName).distinct().toList();
        stats.setOutOfStockCommodities(outOfStock);
        stats.setTotalOutOfStock(outOfStock.size());

        List<String> toReorder = records.stream()
            .filter(r -> Optional.ofNullable(r.getQuantityToOrder()).orElse(0) > 0)
            .map(CommodityRecordDTO::getCommodityName).distinct().toList();
        stats.setCommoditiesToReorder(toReorder);

        List<String> inExcess = records.stream()
            .filter(r -> Optional.ofNullable(r.getExcessQuantityReturned()).orElse(0) > 0)
            .map(CommodityRecordDTO::getCommodityName).distinct().toList();
        stats.setCommoditiesInExcess(inExcess);

        List<String> slowMoving = records.stream()
            .filter(r -> Optional.ofNullable(r.getQuantityConsumed()).orElse(0) < 5)
            .map(CommodityRecordDTO::getCommodityName).distinct().toList();
        stats.setSlowMovingCommodities(slowMoving);

        Map<String, Double> forecast = records.stream()
            .filter(r -> r.getQuantityConsumed() != null && r.getConsumptionPeriod() != null && r.getConsumptionPeriod() > 0)
            .collect(Collectors.groupingBy(
                CommodityRecordDTO::getCommodityName,
                Collectors.averagingDouble(r -> r.getQuantityConsumed() * 30.0 / r.getConsumptionPeriod())
            ));
        stats.setForecast(forecast);

        // Advice
        StringBuilder advice = new StringBuilder();
        boolean hasThisMonthRecord = records.stream().anyMatch(r ->
            r.getRecordDate() != null && !r.getRecordDate().isBefore(startOfMonth) && !r.getRecordDate().isAfter(endOfMonth));
        if (!hasThisMonthRecord) advice.append("No records submitted for this month. ");
        if (!toReorder.isEmpty()) advice.append("Reorder: ").append(String.join(", ", toReorder)).append(". ");
        if (!inExcess.isEmpty()) advice.append("Excess: ").append(String.join(", ", inExcess)).append(". ");
        if (!slowMoving.isEmpty()) advice.append("Slow moving: ").append(String.join(", ", slowMoving)).append(". ");
        if (!outOfStock.isEmpty()) advice.append("Out of stock: ").append(String.join(", ", outOfStock)).append(". ");
        if (advice.length() == 0) advice.append("All commodities are well managed.");

        stats.setAdvice(advice.toString());
        dto.setStats(stats);
        chpDtos.add(dto);
    }

    // CHA-level stats
    List<CommodityRecordDTO> allRecords = chpDtos.stream()
        .flatMap(c -> c.getCommodityRecords().stream())
        .collect(Collectors.toList());

    ChaDashboardStatsDTO chaStats = new ChaDashboardStatsDTO();
    chaStats.setTotalRecords(allRecords.size());
    chaStats.setTotalIssued(allRecords.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityIssued()).orElse(0)).sum());
    chaStats.setTotalConsumed(allRecords.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityConsumed()).orElse(0)).sum());
    chaStats.setTotalExpired(allRecords.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityExpired()).orElse(0)).sum());
    chaStats.setTotalDamaged(allRecords.stream().mapToInt(r -> Optional.ofNullable(r.getQuantityDamaged()).orElse(0)).sum());
    chaStats.setTotalClosingBalance(allRecords.stream().mapToInt(r -> Optional.ofNullable(r.getClosingBalance()).orElse(0)).sum());

    List<String> chpsNoRecordThisMonth = chpDtos.stream()
        .filter(c -> c.getCommodityRecords().stream()
            .noneMatch(r -> r.getRecordDate() != null &&
                !r.getRecordDate().isBefore(startOfMonth) &&
                !r.getRecordDate().isAfter(endOfMonth)))
        .map(ChpDashboardDTO::getChpUsername).toList();

    StringBuilder chaAdvice = new StringBuilder();
    if (!chpsNoRecordThisMonth.isEmpty()) {
        chaAdvice.append("CHP(s) ")
            .append(String.join(", ", chpsNoRecordThisMonth))
            .append(" have not submitted records for this month.");
    }

    ChaDashboardResponseDTO response = new ChaDashboardResponseDTO();
    response.setChps(chpDtos);
    response.setStats(chaStats);
    response.setAdvice(chaAdvice.toString());
    return response;
}

}