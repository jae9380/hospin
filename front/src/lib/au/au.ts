// src/lib/au.ts
import { writable, get } from 'svelte/store';
import { goto } from '$app/navigation';
import { browser } from '$app/environment';
import createClient from 'openapi-fetch';
import type { paths, components } from '$lib/types/api/v1/schema';

type GenderStr = 'MALE' | 'FEMALE' | 'UNDEFINED';
const toGenderString = (c: number): GenderStr =>
	c === 0 ? 'MALE' : c === 1 ? 'FEMALE' : 'UNDEFINED';
const toGenderCode = (s: GenderStr): number => (s === 'MALE' ? 0 : s === 'FEMALE' ? 1 : 2);

export class Au {
	// 내부 스토어
	private _id = writable(0);
	private _identifier = writable('');
	private _name = writable('');
	private _phone = writable('');
	private _email = writable('');
	private _birth = writable('');
	private _genderCode = writable(2);
	private _logined = writable(false);

	// 외부에 노출되는 뷰(게터/세터). this 바인딩 금지 → 캡처 사용.
	public member = (() => {
		const id = this._id,
			identifier = this._identifier,
			name = this._name;
		const phone = this._phone,
			email = this._email,
			birth = this._birth,
			genderCode = this._genderCode;
		const logined = this._logined;
		return {
			get id() {
				return get(id);
			},
			set id(v: number) {
				id.set(v);
			},
			get identifier() {
				return get(identifier);
			},
			set identifier(v: string) {
				identifier.set(v);
			},
			get name() {
				return get(name);
			},
			set name(v: string) {
				name.set(v);
			},
			get phoneNumber() {
				return get(phone);
			},
			set phoneNumber(v: string) {
				phone.set(v);
			},
			get email() {
				return get(email);
			},
			set email(v: string) {
				email.set(v);
			},
			get birth() {
				return get(birth);
			},
			set birth(v: string) {
				birth.set(v);
			},
			get gender() {
				return toGenderString(get(genderCode));
			},
			set gender(v: GenderStr) {
				genderCode.set(toGenderCode(v));
			},
			get logined() {
				return get(logined);
			}
		};
	})();

	private api() {
		return createClient<paths>({
			baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
			credentials: 'include',
			headers: { 'Content-Type': 'application/json' }
		});
	}

	public async initAuth() {
		if (!browser) return;
		try {
			const { data } = await this.api().GET('/api/member');
			if (data?.data) {
				this.setLogined(data.data);
				this._logined.set(true);
			} else {
				this._logined.set(false);
			}
		} catch (e) {
			this._logined.set(false);
		}
	}

	public setLogined(dto: components['schemas']['MemberDto']) {
		// 숫자 성별 수용. 필드명은 실제 스키마에 맞춰 조정.
		this.member.id = dto.id as number;
		this.member.identifier = dto.identifier as string;
		this.member.name = dto.name as string;
		this.member.phoneNumber = dto.phoneNumber as string;
		this.member.email = dto.email as string;
		// dto.gender 또는 dto.genderCode 중 하나를 사용
		const g = (dto as any).genderCode ?? (dto as any).gender;
		this.member.gender = toGenderString(typeof g === 'number' ? g : toGenderCode(g));
		this.member.birth = (dto as any).birth ?? '';
	}

	public setLogout() {
		this.member.id = 0;
		this.member.identifier = '';
		this.member.name = '';
		this.member.phoneNumber = '';
		this.member.email = '';
		this.member.gender = 'UNDEFINED';
		this.member.birth = '';
		this._logined.set(false);
	}

	public isLogin() {
		return get(this._logined);
	}

	public isLogout() {
		return !this.isLogin();
	}

	public goTo(url: string) {
		if (browser) goto(url, { replaceState: false });
	}
	public replace(url: string) {
		if (browser) goto(url, { replaceState: true });
	}

	public msgInfo(msg: string) {
		if (browser) window.alert(msg);
	}
	public msgError(msg: string) {
		if (browser) window.alert(msg);
	}

	public async logoutAndRedirect(url: string) {
		if (!browser) return;
		await this.api().POST('/api/member/logout', { credentials: 'include' });
		this.setLogout();
		this.replace(url);
	}

	public getGoogleLoginUrl() {
		const redirect = `${import.meta.env.VITE_CORE_FRONT_BASE_URL}/member/socialLoginCallback?providerTypeCode=google`;
		return `${import.meta.env.VITE_CORE_API_BASE_URL}/member/socialLogin/google?redirectUrl=${encodeURIComponent(redirect)}`;
	}

	public async initAuthAndRedirectOk(url: string) {
		await this.initAuth();
		if (this.isLogin()) this.replace(url);
	}
}

// SSR 누수 방지: 브라우저에서만 싱글턴 생성
export const au = browser ? new Au() : null;
