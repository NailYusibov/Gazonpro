package com.gitlab;

import com.gitlab.dto.*;
import com.gitlab.enums.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TestUtil {

    private static final Random random = new Random();

    public static BankCardDto generateBankCardDto() {
        long min = 10000000L;
        long max = 1000000000L;

        BankCardDto bankCardDto = new BankCardDto();

        bankCardDto.setCardNumber(String.valueOf(random.nextLong(max - min + 1L) + min));
        bankCardDto.setDueDate(LocalDate.now());
        bankCardDto.setSecurityCode(123);

        return bankCardDto;
    }

    public static Set<BankCardDto> generateBankCardDtos() {
        Set<BankCardDto> bankCardSet = new HashSet<>();
        bankCardSet.add(generateBankCardDto());
        bankCardSet.add(generateBankCardDto());
        bankCardSet.add(generateBankCardDto());
        return bankCardSet;
    }

    public static ExampleDto generateExampleDto() {
        ExampleDto exampleDto = new ExampleDto();
        exampleDto.setExampleText("testExample");
        return exampleDto;
    }

    public static OrderDto generateOrderDto(long userId, ShippingAddressDto shippingAddressDto) {
        long min = 1L;
        long max = 10000L;

        OrderDto orderDto = new OrderDto();

        orderDto.setShippingAddressDto(shippingAddressDto);
        orderDto.setShippingDate(LocalDate.parse("2027-05-01"));
        orderDto.setOrderCode(String.valueOf(random.nextLong(max - min + 1L) + min));
        orderDto.setCreateDateTime(LocalDateTime.now());
        orderDto.setSum(new BigDecimal(5));
        orderDto.setDiscount(new BigDecimal(6));
        orderDto.setBagCounter((byte) 5);
        orderDto.setUserId(userId);
        orderDto.setSelectedProducts(Set.of(new SelectedProductDto()));
        orderDto.setOrderStatus(OrderStatus.DONE);

        return orderDto;
    }

    public static PassportDto generatePassportDto() {
        long minLeft = 1000L;
        long maxLeft = 9999L;
        long minRight = 100000L;
        long maxRight = 999999L;

        PassportDto passportDto = new PassportDto();

        passportDto.setCitizenship(Citizenship.RUSSIA);
        passportDto.setFirstName("Ivan");
        passportDto.setLastName("Petrov");
        passportDto.setPatronym("Aleksandrovich");
        passportDto.setBirthDate(LocalDate.of(2000, 5, 20));
        passportDto.setIssueDate(LocalDate.of(2014, 6, 10));

        passportDto.setPassportNumber(
                (random.nextLong(maxLeft - minLeft + 1L) + minLeft) + " " +
                        (random.nextLong(maxRight - minRight + 1L) + minRight));

        passportDto.setIssuer("MVD RUSSIA №10 in Moscow");
        passportDto.setIssuerNumber("123-456");

        return passportDto;
    }

    public static PaymentDto generatePaymentDto(long orderId, long userId, BankCardDto bankCardDtoInBD) {
        PaymentDto paymentDto = new PaymentDto();

        paymentDto.setBankCardDto(bankCardDtoInBD);
        paymentDto.setPaymentStatus(PaymentStatus.PAID);
        paymentDto.setCreateDateTime(LocalDateTime.now());
        paymentDto.setOrderId(orderId);
        paymentDto.setSum(new BigDecimal(500));
        paymentDto.setUserId(userId);

        return paymentDto;
    }

    public static PersonalAddressDto generatePersonalAddressDto() {
        PersonalAddressDto personalAddressDto = new PersonalAddressDto();

        personalAddressDto.setAddress("testAddress");
        personalAddressDto.setDirections("testDirections");
        personalAddressDto.setApartment("111");
        personalAddressDto.setFloor("14");
        personalAddressDto.setEntrance("7");
        personalAddressDto.setDoorCode("1244");
        personalAddressDto.setPostCode("123446");

        return personalAddressDto;
    }

    public static PickupPointDto generatePickupPointDto() {
        PickupPointDto pickupPointDto = new PickupPointDto();

        pickupPointDto.setAddress("testAddress");
        pickupPointDto.setDirections("testDirections");
        pickupPointDto.setShelfLifeDays((byte) 16);
        pickupPointDto.setPickupPointFeatures(Set.of(PickupPointFeatures.values()));

        return pickupPointDto;
    }

    public static PostomatDto generatePostomatDto() {
        PostomatDto postomatDto = new PostomatDto();
        postomatDto.setAddress("TestAddress");
        postomatDto.setDirections("TestDirections");
        postomatDto.setShelfLifeDays((byte) 10);
        return postomatDto;
    }

    public static ProductCategoryDto generateProductCategoryDto() {
        long min = 1L;
        long max = 10000L;

        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        productCategoryDto.setName(String.valueOf(random.nextLong(max - min + 1L) + min));
        return productCategoryDto;
    }

    public static ProductImageDto generateProductImageDto(long productId) throws IOException {
        ProductImageDto productImageDto = new ProductImageDto();

        productImageDto.setProductId(productId);
        productImageDto.setName("file.txt");
        productImageDto.setData(getBytesFromImage());

        return productImageDto;
    }

    public static ProductDto generateProductDto() {
        long min = 1L;
        long max = 10000L;

        ProductDto productDto = new ProductDto();

        productDto.setName("test");
        productDto.setStockCount(1);
        productDto.setDescription("test");
        productDto.setIsAdult(true);
        productDto.setCode("testCode" + (random.nextLong(max - min + 1L) + min));
        productDto.setWeight(1L);
        productDto.setPrice(BigDecimal.ONE);
        productDto.setReview(new ArrayList<>());

        return productDto;
    }

    public static RoleDto generateRoleDto() {
        long min = 1L;
        long max = 10000L;

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("ROLE_TEST" + (random.nextLong(max - min + 1L) + min));

        return roleDto;
    }

    public static Set<RoleDto> generateRoleDtos() {
        Set<RoleDto> roleDtos = new HashSet<>();
        roleDtos.add(generateRoleDto());
        roleDtos.add(generateRoleDto());
        roleDtos.add(generateRoleDto());
        return roleDtos;
    }

    public static SelectedProductDto generateSelectedProductDto(long productId, long userId) {
        SelectedProductDto selectedProductDto = new SelectedProductDto();

        selectedProductDto.setProductId(productId);
        selectedProductDto.setCount(111);
        selectedProductDto.setSum(BigDecimal.ONE);
        selectedProductDto.setTotalWeight(111L);
        selectedProductDto.setUserId(userId);
        selectedProductDto.setIsSelected(true);

        return selectedProductDto;
    }

    public static Set<ShippingAddressDto> generateShippingAddressDtos() {
        Set<ShippingAddressDto> personalAddress = new HashSet<>();
        personalAddress.add(generatePersonalAddressDto());
        personalAddress.add(generatePersonalAddressDto());
        personalAddress.add(generatePersonalAddressDto());
        return personalAddress;
    }

    public static ReviewDto generateReviewDto(Long productId) {
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setProductId(productId);
        reviewDto.setCreateDate(LocalDate.now());
        reviewDto.setPros("testPros");
        reviewDto.setCons("testCons");
        reviewDto.setComment("testComment");
        reviewDto.setRating((byte) 1);
        reviewDto.setHelpfulCounter(11);
        reviewDto.setNotHelpfulCounter(1);
        reviewDto.setUserId(3L);

        return reviewDto;
    }

    public static ReviewImageDto generateReviewImageDto(Long reviewId) throws IOException {
        ReviewImageDto reviewImageDto = new ReviewImageDto();

        reviewImageDto.setReviewId(reviewId);
        reviewImageDto.setName("test.txt");
        reviewImageDto.setData(getBytesFromImage());

        return reviewImageDto;
    }

    public static ShoppingCartDto generateShoppingCartDto(long userId) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();

        shoppingCartDto.setUserId(userId);
        shoppingCartDto.setSelectedProducts(Set.of(new SelectedProductDto()));
        shoppingCartDto.setSum(BigDecimal.valueOf(100));
        shoppingCartDto.setTotalWeight(500L);

        return shoppingCartDto;
    }

    public static StoreDto generateStoreDto(long ownerId, Set<Long> managersId) {
        StoreDto storeDto = new StoreDto();
        storeDto.setOwnerId(ownerId);
        storeDto.setManagersId(managersId);
        return storeDto;
    }

    public static UserDto generateUserDto() {
        String email = "@mail.ru";
        long min = 1L;
        long max = 100000;

        UserDto userDto = new UserDto();
        userDto.setEmail((random.nextLong(max - min + 1L) + min) + email);
        userDto.setUsername("username" + (random.nextLong(max - min + 1L) + min));
        userDto.setPassword("user");
        userDto.setSecurityQuestion("answer");
        userDto.setAnswerQuestion("question");
        userDto.setFirstName("user");
        userDto.setLastName("user");
        userDto.setBirthDate(LocalDate.now());
        userDto.setGender(Gender.MALE);
        userDto.setPhoneNumber(String.valueOf(random.nextLong(max - min + 1L) + min));
        userDto.setPassportDto(generatePassportDto());
        userDto.setShippingAddressDtos(generateShippingAddressDtos());
        userDto.setBankCardDtos(generateBankCardDtos());

        userDto.setRoles(generateRoleDtos().stream()
                .map(RoleDto::getRoleName)
                .collect(Collectors.toSet()));

        return userDto;
    }

    public static Set<UserDto> generateUserDtos() {
        Set<UserDto> userDtoSet = new HashSet<>();

        userDtoSet.add(generateUserDto());
        userDtoSet.add(generateUserDto());
        userDtoSet.add(generateUserDto());

        return userDtoSet;
    }

    public static WorkingScheduleDto generateWorkingScheduleDto() {
        WorkingScheduleDto workingScheduleDto = new WorkingScheduleDto();

        workingScheduleDto.setFrom(LocalTime.of(10, 0));
        workingScheduleDto.setTo(LocalTime.of(18, 0));
        workingScheduleDto.setDayOfWeek(DayOfWeek.WEDNESDAY);

        return workingScheduleDto;
    }

    public static byte[] getBytesFromImage() throws IOException {
        BufferedImage image = ImageIO.read(new File("src/test/resources/image/product.png"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
