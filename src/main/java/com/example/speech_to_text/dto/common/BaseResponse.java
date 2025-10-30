package com.example.speech_to_text.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {

	private String code;

	private String message;

	private T data;
	

	/**
	 * Using for save history in db
	 */

	public static BaseResponse<?> ok() {
		return ok(null);
	}

	public static <T> BaseResponse<T> ok(T data) {
		return ok("COMMON_OK", "success", data);
	}


	public static <T> BaseResponse<T> ok(String code, String messsage) {
		return new BaseResponse<>(code, messsage, null);
	}

	public static <T> BaseResponse<T> ok(String code, String messsage, T data) {
		return new BaseResponse<>(code, messsage, data);
	}

	public static BaseResponse<?> error(String messsage) {
		return error(messsage, null);
	}

	public static <T> BaseResponse<T> error(String messsage, T data) {
		return new BaseResponse<>("COMMON_ERROR", messsage, data);
	}


	public static <T> BaseResponse<T> error(String code, String messsage) {
		return new BaseResponse<>(code, messsage, null);
	}


	public static <T> BaseResponse<T> error(String code, String messsage, T data) {
		return new BaseResponse<>(code, messsage, data);
	}

	public static <T> BaseResponse<T> errorWithType(String message) {
		return new BaseResponse<>("COMMON_ERROR", message, null);
	}
}
