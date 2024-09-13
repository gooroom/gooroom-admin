<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>GPMS OTP Login</title>
    <script src="/gpms/plugins/js-qrcode/qrcode.min.js"></script>
    <style>
        body {
            background-color: #e6e6e6;
            font-family: 'Ubuntu', sans-serif;
        }

        .main {
            background: linear-gradient(to bottom, #ffffff, #d2d2d2);
            width: 523px;
            height: 692px;
            margin: auto;
            border-radius: 0.5em;
            box-shadow: 0px 11px 35px 2px rgba(0, 0, 0, 0.14);
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        .saved-main {
            background: linear-gradient(to bottom, #ffffff, #d2d2d2);
            width: 523px;
            height: 500px;
            margin: auto;
            border-radius: 0.5em;
            box-shadow: 0px 11px 35px 2px rgba(0, 0, 0, 0.14);
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        .sign {
            padding-top: 40px;
            margin-bottom: 0px;
            color: #337dd5;
            font-weight: bold;
            font-size: 40px;
        }

        .version {
            padding-top: 4px;
            margin-top: 0px;
            margin-bottom: 20px;
            color: #337dd5;
            font-weight: bold;
            font-size: 13px;
        }

        .sysmsg {
            padding-top: 4px;
            margin-top: 0px;
            margin-bottom: 20px;
            color: #d53333;
            font-weight: bold;
            font-size: 13px;
        }

        form.authForm {
            display: flex;
            flex-direction: row;
            justify-content: center;
            gap: 10px;
            flex-wrap: wrap; /* Change to wrap */
        }

        .authInput {
            width: 40px;
            height: 54px;
            text-align: center;
            font-size: 1.5em;
            border: 2px solid #d2d2d2;
            border-radius: 0.25em;
            margin: 5px;
        }

        .submit:hover {
            background-color: #45a049;
        }

        .submit {
            width: 70%;
            cursor: pointer;
            color: #fff;
            background: linear-gradient(to right, #2757b0, #40a5fb);
            border: 0;
            padding-left: 40px;
            padding-right: 40px;
            padding-bottom: 10px;
            padding-top: 10px;
            font-family: 'Ubuntu', sans-serif;
            margin-top: 20px;
            font-size: 16px;
            box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
        }

        .cont {
            text-shadow: 0px 0px 3px rgba(117, 117, 117, 0.12);
            color: #9c9c9c;
            padding-top: 16px;
            text-align: center;
        }

        a {
            text-shadow: 0px 0px 3px rgba(117, 117, 117, 0.12);
            color: #E1BEE7;
            text-decoration: none
        }

        @media (max-width: 600px) {
            .main {
                border-radius: 0px;
            }
        }

        .page-title {
            font-weight: 700;
            font-size: 20px;
        }

        .page-title-saved {
            font-weight: 700;
            font-size: 16px;
        }
        
        .typo-gray {
            color: #999999;
            font-weight: 400;
            font-size:16px;
        }

        .step-container {
            display: flex;
            margin-top: 30px;
            margin-left: auto;
            margin-right: auto;
            width: 90%;
        }

        .step-content {
            margin-left: 10px;
            display: inline-block;
            font-weight: 400;
            font-size: 14px;
        }

        .step-title {
            display: inline-block;
            font-weight: 500;
            font-size: 16px;
            color: #001AFF;
        }

        .step-description {
            display: inline-block;
            font-size: 16px;
            color: #333333;
            font-weight: 400;
            margin: 5px 0 10px;
        }

        .saved-description {
            justify-content: center;
            font-size: 16px;
            color: #333333;
            font-weight: 400;
            margin: 5px 0 10px;
        }


        .code-group {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 20px;
        }

        .qr-code-section {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
        }

        .qr-code-wrapper, .manual-code-wrapper {
            display: flex;
            flex-direction: column;
            align-items: center; 
        }

        .qr-code-wrapper {
            margin-right: 40px;
        }

        .qr-code {
            margin-bottom: 10px;
        }

        .manual-code {
            background-color: #f0f0f0;
            padding: 10px 15px;
            font-size: 18px;
            border-radius: 5px;
            font-weight: bold;
            letter-spacing: 2px;
            margin-bottom: 10px;
        }

        .label-qr-code, .label-manual-code {
            font-size: 12px;
            color: #666666;
            text-align: center;
        }

        .otp-inputs {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
        }

    </style>
</head>
<body>
<% if("false".equals(request.getAttribute("saved"))) { %>
    <div class="main">
        <p class="page-title" align="center"> Google OTP 등록 </p>
        <p class="typo-gray" align="center"> 2차 인증 사용을 위해 앱스토어에서<br>‘Google Authenticator’ 앱을 다운로드 하시기 바랍니다. </p>
        <div class="step-container">
            <div class="step-content">
                <p class="step-title"> step 1 </p>
                <p class="step-description"> 본인 명의 모바일로 Google Authenticator를 실행하세요. </p>
            </div>

        </div>
        <div class="step-container">
            <div class="step-content">
                <p class="step-title"> Step 2 </p>
                <p class="step-description"> 아래 QR코드 스캔 또는 수동 코드를 입력하세요. </p>
                <div class="code-group">
                    <div class="qr-code-section">
                        <div class="qr-code-wrapper">
                            <div class="qr-code" id="qrcode"></div>
                            <span class="label-qr-code">QR코드</span>
                        </div>
                        <div class="manual-code-wrapper">
                            <div class="manual-code" id="manual-code"></div>
                            <span class="label-manual-code">수동 코드</span>
                        </div>
                    </div>
                </div>        
            </div>
        </div>

        <div class="step-container">
            <div class="step-content">
                <p class="step-title"> Step 3 </p>
                <p class="step-description"> 2차 인증번호를 아래에 입력하세요. </p>
                <form id="authForm" class="authForm" name='f' action="/gpms/otp/authenticate" method='POST' onSubmit="make_code()">
                    <input type="hidden" id="codeInput" name="userCode">
                    <div class="otp-inputs">
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="1" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="2" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="3" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="4" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="5" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="6" required>
                    </div>
                    <button class="submit" type="submit">등록</button>
                </form>
            </div>
        </div>
        <p class="cont">Gooroom Platform Management Server</p>
    </div>
<% } else { %>
    <div class="saved-main">
        <p class="sign" align="center">GPMS</p>
        <div class="step-container">
            <div class="step-content">
                <p class="saved-description" align="center"> Google Authenticator 앱(APP) 실행 후 <br> [ 2차 인증번호 ]를 입력하세요. </p>
                <form id="authForm" class="authForm" name='f' action="/gpms/otp/authenticate" method='POST' onSubmit="make_code()">
                    <input type="hidden" id="codeInput" name="userCode">
                    <div class="otp-inputs">
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="1" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="2" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="3" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="4" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="5" required>
                        <input class="authInput" name="authInput" type="password" maxlength="1" pattern="[0-9]" tabIndex="6" required>
                    </div>
                     <p class="sysmsg" align="center">${msg}</p>
                    <button class="submit" type="submit">등록</button>
                </form>
            </div>
        </div>
        <p class="cont">Gooroom Platform Management Server</p>
    </div>
<% } %>
<% if("false".equals(request.getAttribute("saved"))) { %>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            fetch("/gpms/otp/generate")
                .then(response => response.json())
                .then(data => {
                    new QRCode(document.getElementById("qrcode"), {
                        text: data.data[0].url,
                        width: 112,
                        height: 108,
                    });

                    const secret = data.data[0].secret;
                    document.querySelector('.manual-code').textContent = secret;
                })
                .catch(error => console.error('Fetch error:', error));

            var authInputs = document.querySelectorAll('.authInput');
            if (authInputs.length > 0) {
                authInputs[0].focus();
            }

            for (var i = 0; i < authInputs.length; i++) {
                authInputs[i].addEventListener('keyup', function(event) {
                    var index = Array.prototype.indexOf.call(authInputs, this);
                    if (this.value.length === 1 && index < authInputs.length - 1) {
                        authInputs[index + 1].focus();
                    }
                    if (event.key === "Backspace" && this.value.length === 0 && index > 0) {
                        authInputs[index - 1].focus();
                    }
                });
            }
        });
    </script>
<% } else { %>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var authInputs = document.querySelectorAll('.authInput');
            if (authInputs.length > 0) {
                authInputs[0].focus();
            }

            for (var i = 0; i < authInputs.length; i++) {
                authInputs[i].addEventListener('keyup', function(event) {
                    var index = Array.prototype.indexOf.call(authInputs, this);
                    if (this.value.length === 1 && index < authInputs.length - 1) {
                        authInputs[index + 1].focus();
                    }
                    if (event.key === "Backspace" && this.value.length === 0 && index > 0) {
                        authInputs[index - 1].focus();
                    }
                });
            }
        });
    </script>
<% } %>
<script>
    function make_code() {
        var inputs = document.querySelectorAll('.authInput');
        var code = '';
        inputs.forEach(function(input) {
            code += input.value.trim();
        });
        document.getElementById('codeInput').value = code;
    }
</script>
</body>
</html>
