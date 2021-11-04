package kr.ds.helper.web

import android.webkit.WebViewClient.*

class DefaultWebViewErrorHelper {
    private val errCodeSet = setOf(
        ERROR_AUTHENTICATION, // 서버에서 사용자 인증 실패
        ERROR_BAD_URL,  // 잘못된 URL
        ERROR_CONNECT,  // 서버로 연결 실패
        ERROR_FAILED_SSL_HANDSHAKE,  // SSL handshake 수행 실패
        ERROR_FILE,  // 일반 파일 오류
        ERROR_FILE_NOT_FOUND, // 파일을 찾을 수 없습니다
        ERROR_HOST_LOOKUP, // 서버 또는 프록시 호스트 이름 조회 실패
        ERROR_IO, // 서버에서 읽거나 서버로 쓰기 실패
        ERROR_PROXY_AUTHENTICATION, // 프록시에서 사용자 인증 실패
        ERROR_REDIRECT_LOOP, // 너무 많은 리디렉션
        ERROR_TIMEOUT, // 연결 시간 초과
        ERROR_TOO_MANY_REQUESTS, // 페이지 로드중 너무 많은 요청 발생
        ERROR_UNKNOWN, // 일반 오류
        ERROR_UNSUPPORTED_AUTH_SCHEME, // 지원되지 않는 인증 체계
        ERROR_UNSUPPORTED_SCHEME,
    )

    fun isDefinedError(errCode: Int): Boolean {
        return errCodeSet.contains(errCode)
    }
}