export async function loadProtectedData(endpoint, resultId) {
    try {
        // 1차 요청 (access_token 포함된 쿠키 자동 전송됨)
        const res = await fetch(endpoint, {
            method: "GET",
            credentials: "include" // 쿠키 전송 위해 필요
        });

        if (res.status === 401) {
            // access token 만료 → refresh 시도
            const refreshRes = await fetch("http://localhost:9000/token/refresh", {
                method: "POST",
                credentials: "include"
            });

            if (refreshRes.ok) {
                // 재요청
                const retryRes = await fetch(endpoint, {
                    method: "GET",
                    credentials: "include"
                });

                if (retryRes.ok) {
                    const text = await retryRes.text();
                    document.getElementById(resultId).innerText = text;
                } else {
                    window.location.href = "http://localhost:9000/login";
                }
            } else {
                window.location.href = "http://localhost:9000/login";
            }

        } else if (res.ok) {
            const text = await res.text();
            document.getElementById(resultId).innerText = text;
        } else {
            // 기타 오류 처리
            console.error("요청 실패:", res.status);
        }
    } catch (error) {
        console.error("요청 중 예외 발생:", error);
        window.location.href = "http://localhost:9000/login";
    }
}