package org.example.final_btl_datve.service.impl;

import jakarta.persistence.Tuple;
import org.example.final_btl_datve.dto.BookingHistoryDto;
import org.example.final_btl_datve.dto.UserDto;
import org.example.final_btl_datve.entity.User;
import org.example.final_btl_datve.repository.UserRepository;
import org.example.final_btl_datve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .username(user.getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .address(user.getAddress())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .role(user.getUserRoles().get(0).getRole().getRoleName())
                .createdAt(user.getCreatAt())
                .membershipType(user.getMembership() == null?
                null : user.getMembership().getMembership_type())
                .build();
    }

    //Tạo admin
    @Override
    public UserDto create (UserDto userDto) throws Exception {
        if(userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new Exception("Email đã tồn tại");
        }
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .gender(userDto.getGender())
                .birthday(userDto.getBirthday())
                .build();
        return convertToDto(userRepository.save(user));
    }

    // Đọc tất cả người dùng
    @Override
    public List<UserDto> reads () {
        return userRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public UserDto read (Long userID) throws Exception {
        return userRepository.findById(userID).map(this::convertToDto).orElseThrow(() -> {
            return new Exception("Người dùng không tồn tại");
        });
    }

    // Cập nhật thông tin người dùng
    @Override
    public UserDto update (Long userID, UserDto userDto) throws Exception {
        User existingUser = userRepository.findById(userID).orElseThrow(() -> {
            return new Exception("Người dùng không tồn tại");
        });

        User updatedUser = existingUser.toBuilder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .birthday(userDto.getBirthday())
                .build();
        return convertToDto(userRepository.save(updatedUser));
    }

    // Xóa người dùng
    @Override
    public void delete (Long userID) throws Exception {
        if(!userRepository.existsById(userID)) {
            throw new Exception("Người dùng không tồn tại");
        }
        userRepository.deleteById(userID);
    }

    //
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    // Tìm kiếm người dùng theo từ khóa
    @Override
    public List<UserDto> search(String keyword) {
        return userRepository.search(keyword).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Tìm kiếm người dùng theo ID
    @Override
    public UserDto searchId(Long userID) throws Exception {
        return userRepository.findById(userID).map(this::convertToDto).orElseThrow(() -> {
            return new Exception("Người dùng không tồn tại");
        });
    }

    // Xem lịch sử đặt vé
    @Override
    public List<BookingHistoryDto> getBookingHistory(Long userID) throws Exception {
        List<Tuple> results = userRepository.getBookingHistory(userID);

        List<BookingHistoryDto> bookingHistoryDtos = results.stream().map(result -> {
            return BookingHistoryDto.builder()
                    .movieName(result.get("movieName", String.class))
                    .cinemaName(result.get("cinemaName", String.class))
                    .roomName(result.get("roomName", String.class))
                    .bookingTime(result.get("bookingTime", Timestamp.class))
                    .seatNames(result.get("seatNames", String.class))
                    .comboDetails(result.get("comboDetails", String.class))
                    .seatCount(result.get("seatCount", Long.class))
                    .build();
        }).collect(Collectors.toList());
        return bookingHistoryDtos;
    }
}

