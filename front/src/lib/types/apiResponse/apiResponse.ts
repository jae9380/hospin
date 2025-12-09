export interface ApiResponse<T> {
	statusCode: number;
	message: string;
	resultType: 'SUCCESS' | 'VALIDATION_EXCEPTION' | 'CUSTOM_EXCEPTION';
	errorCode?: string;
	data: T;
}
