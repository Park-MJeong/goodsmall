package com.goodsmall.modules.cart.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.modules.cart.domain.CartProductRepository;
import com.goodsmall.modules.cart.domain.CartRepository;
import com.goodsmall.modules.cart.domain.entity.Cart;
import com.goodsmall.modules.cart.domain.entity.CartProducts;
import com.goodsmall.modules.cart.dto.CartListDto;
import com.goodsmall.modules.cart.dto.CartProductAddRequestDto;
import com.goodsmall.modules.cart.dto.CartProductDto;
import com.goodsmall.modules.cart.dto.CartProductUpdateRequestDto;
import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.domain.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    /**
     * 장바구니 내 상품리스트 조회 ,dto에 담아서 전달
     * */
    public ApiResponse<?> getCart(Long cartId) {
        Cart cartList = getCartById(cartId);
        CartListDto listDto = new CartListDto(cartList);
        return ApiResponse.success(listDto);
    }
    /**
     * 장바구니 내 상품 추가
     * */

    @Transactional
    public ApiResponse<?> updateCart(Long cartId, CartProductAddRequestDto dto){
        Cart cart=getCartById(cartId);
        Product product =getProduct(dto.getProductId());

        if(isProductAlreadyInCart(cart,product)){
            System.out.println();
            throw new BusinessException(ErrorCode.CART_PRODUCT_ALREADY);
        }

        CartProducts cartProducts = new CartProducts(cart,product,dto.getQuantity());
        cartProductRepository.save(cartProducts);
        CartProductDto cartProductDto = new CartProductDto(cartProducts);
        return ApiResponse.success(cartProductDto);
    }



    /**
     * 장바구니 내 상품정보 수정
     * */
    @Transactional
    public ApiResponse<?> updateProductQuantity(Long cartProductId,CartProductUpdateRequestDto dto) {
        Integer quantity = dto.getQuantity();

        // 장바구니 내 상품정보
        CartProducts cartProduct = getCartProducts(cartProductId);
        Cart cart = cartProduct.getCart();   //장바구니 정보, 수정 후 해당 장바구니 내 상품 리스트 출력위해
        Long productId = cartProduct.getProduct().getId();

        //  삭제 상품 처리
        if(dto.isDelete()){
            return deleteCartProduct(cart, cartProductId);
        }

        // 1. 해당 상품 재고 파악
        /* TODO: 추후 품절상품은 장바구니에서 모두 delete ?? 장바구니에서 삭제처리방법생각 */
        Product product = productRepository.getProduct(productId).orElseThrow(() ->
                new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
//        if(product.getStatus().equals("Sole Out")){
//            deleteCartProduct(cart, cartProductId);
//            return ApiResponse.success("품절된 상품이 장바구니에서 삭제되었습니다.");
//        }

//        2.수량 파악,예외처리
        if(product.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }

        log.info("수량 변경 전  {}",cartProduct.getQuantity());
        //3. 수량변경
        cartProduct.setQuantity(quantity);
        cartProductRepository.save(cartProduct);

        log.info("수량 변경 후  {}",cartProduct.getQuantity());

        CartListDto listDto = new CartListDto(cart);
        return ApiResponse.success(listDto);
    }


//    상품 삭제
    @Transactional
    public ApiResponse<?> deleteCartProduct(Cart cart,Long cartProductId) {

        cartProductRepository.delete(cartProductId);
        Cart updateCart = getCartById(cart.getId());

        if (updateCart ==null) {
            return ApiResponse.success("장바구니가 비었습니다.");
        }
        CartListDto listDto = new CartListDto(updateCart);
        return ApiResponse.success(listDto);
    }

    /**
     * 장바구니 내 상품리스트 조회
     * */
    private Cart getCartById(Long cartId){
        log.info("getCartById:{}",cartRepository.getCart(cartId));
        return cartRepository.getCart(cartId);
    }
    /**
     * 장바구니 상품 테이블의 정보 (개별 상품)
     * */
    private CartProducts getCartProducts(Long cartProductId){
        return cartProductRepository.getCartProducts(cartProductId).orElseThrow(
                ()->new BusinessException(ErrorCode.CART_PRODUCT_NOT_FOUND));
    }


    private Product getProduct(Long productId) {
        return productRepository.getProduct(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private boolean isProductAlreadyInCart(Cart cart,Product product) {
        return cartProductRepository.isProductAlreadyInCart(cart,product);
    }
}
