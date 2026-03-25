export function validatePasswordRule(value: string): boolean {
	// 8~20자, 공백 불가, 허용문자: 영문/숫자/특수문자
	const regex = /^[A-Za-z0-9!@#$%^&*()_+{}\[\]:;<>,.?~\\/-]{8,20}$/;
	return regex.test(value);
}
