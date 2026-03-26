export interface ApiResponse<T> {
	statusCode: number;
	message: string;
	resultType: 'SUCCESS' | 'VALIDATION_EXCEPTION' | 'CUSTOM_EXCEPTION';
	errorCode?: string;
	data: T;
}

export function isApiSuccess<T>(response: ApiResponse<T> | null | undefined): boolean {
	return response?.resultType === 'SUCCESS';
}
