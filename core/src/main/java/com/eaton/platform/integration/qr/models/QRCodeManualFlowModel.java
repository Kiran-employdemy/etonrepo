package com.eaton.platform.integration.qr.models;

import com.eaton.platform.core.services.EatonRecaptchaConfigService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class QRCodeManualFlowModel {

    @OSGiService
    EatonRecaptchaConfigService recaptchaConfigService;

    private String reCaptchaSiteKey;

    @Inject @Default(values="Authenticated")
    private String authenticatedTextField;

    @Inject @Default(values="The Product is verified being Authentic: however this does not gurantee the condition or fit for the pusrpose of the product")
    private String authenticatedDescriptionText;

    @Inject @Default(values="Authenticate Product")
    private String authenticateProductText;

    @Inject @Default(values="Enter your 16-17 digit serial number")
    private String enterSerialNumberText;

    @Inject @Default(values="Serial Number")
    private String serialNumberPlaceholder;

    @Inject @Default(values="Serial Number Verified")
    private String serialNumberVerifiedText;

    @Inject @Default(values=" The serial number you entered was not found. Please try again or")
    private String invalidSerialErrorMessage;

    @Inject @Default(values="The serial number is known to Eaton to be suspect, please try again or ")
    private String suspectSerialErrorMessage;

    @Inject @Default(values="The serial number you entered does not match the product. Check your product's serial number and try again.")
    private String serialCatalogMismatchErrorMessage;

    @Inject @Default(values="to navigate to the product page for the serial number you have entered")
    private String serialCatalogMismatchClickHereMessage;

    @Inject @Default(values="report this issue")
    private String reportIssueText;

    @Inject @Default(values="Report an issue")
    private String reportIssueCtaText;

    @Inject @Default(values="furthur assistance.")
    private String furtherAssistenceText;

    @Inject @Default(values="Serial number")
    private String serialNumberText;

    @Inject @Default(values="The serial number you entered should not contain special charachter.")
    private String specialCharacterSerialError;

    @Inject @Default(values="The serial number you entered is not correct it should be of 16 or 17 digit.")
    private String invalidSerialLength;

    @Inject @Default(values="Reset")
    private String resetButttonText;

    @Inject @Default(values="continue")
    private String serialNumberSubmitButtonText;

    @Inject @Default(values="Click")
    private String clickText;

    @Inject @Default(values="here")
    private String hereText;
	
	@Inject @Default(values="Enter the 6 digit authentication code")
    private String authCodeText;
    
    @Inject @Default(values="Authenticate")
    private String authenticateButtonText;
    
    @Inject @Default(values="The product you entered is suspect!  Please try again or ")
    private String suspectAuthCodeErrorMessage;
    
    @Inject @Default(values="The authentication code you enter does not match our records for this serial number. Please try again, or ")
    private String authCodeMismatchErrorMessage;
    
    @Inject @Default(values="Invalid Authentication code. Length must be 6 characters")
    private String authCodeWrongErrorMessage;
    
    @Inject @Default(values="The product is verified as being authentic; however, this does not guarantee the condition or fit for purpose of the product.")
    private String authCodeVerifiedText;
    
    @Inject @Default(values="Authenticated:")
    private String authenticatedText;
    
	@Inject @Default(values="Authentication code is required")
    private String blankAuthError;

	 @Inject @Default(values="Full Name")
    private String fullNameText;
    
    @Inject @Default(values="Email")
    private String emailText;
    
    @Inject @Default(values="Comments")
    private String commentsText;
    
    @Inject @Default(values="Report Issue")
    private String reportIssueButtonText;
    
    @Inject @Default(values="Thank you, your issue has been submitted, our product team will contact with you soon.")
    private String reportIssueSubmitMessageText;
    
	@Inject @Default(values="Please enter a valid email address.")
    private String emailIdMandatoryErrorMessage;
	
	@Inject @Default(values="Report an Issue")
    private String reportIssueTitletext;

	@Inject @Optional
	private String captchaRequiredCheckbox;

    @Inject @Default(values="Complete Captcha challenge before authenticating.")
    private String reCaptchaError;

    @Inject @Optional
    private String checkIconQrCodePath;

    public String getAuthenticatedTextField() {
        return authenticatedTextField;
    }

    public String getAuthenticatedDescriptionText() {
        return authenticatedDescriptionText;
    }

    public String getAuthenticateProductText() {
        return authenticateProductText;
    }

    public String getEnterSerialNumberText() {
        return enterSerialNumberText;
    }

    public String getSerialNumberPlaceholder() {
        return serialNumberPlaceholder;
    }

    public String getSerialNumberVerifiedText() {
        return serialNumberVerifiedText;
    }

    public String getInvalidSerialErrorMessage() {
        return invalidSerialErrorMessage;
    }

    public String getSuspectSerialErrorMessage() {
        return suspectSerialErrorMessage;
    }

    public String getSerialCatalogMismatchErrorMessage() {
        return serialCatalogMismatchErrorMessage;
    }

    public String getSerialCatalogMismatchClickHereMessage() {
        return serialCatalogMismatchClickHereMessage;
    }

    public String getReportIssueText() {
        return reportIssueText;
    }

    public String getReportIssueCtaText() {
        return reportIssueCtaText;
    }

    public String getFurtherAssistenceText() {
        return furtherAssistenceText;
    }

    public String getSerialNumberText() {
        return serialNumberText;
    }

    public String getResetButttonText() {
        return resetButttonText;
    }

    public String getSerialNumberSubmitButtonText() {
        return serialNumberSubmitButtonText;
    }

    public String getClickText() {
        return clickText;
    }

    public String getHereText() {
        return hereText;
    }

	 public String getFullNameText() {
        return fullNameText;
    }

    public String getEmailText() {
        return emailText;
    }

    public String getCommentsText() {
        return commentsText;
    }
    
    public String getReportIssueButtonText() {
        return reportIssueButtonText;
    }
    
    public String getReportIssueSubmitMessageText() {
        return reportIssueSubmitMessageText;
    }
	
    public String getSpecialCharacterSerialError() {
        return specialCharacterSerialError;
    }

    public String getInvalidSerialLength() {
        return invalidSerialLength;
    }
	
	public String getAuthCodeText() {
        return authCodeText;
    }

    public String getAuthenticateButtonText() {
        return authenticateButtonText;
    }
    
    public String getSuspectAuthCodeErrorMessage() {
        return suspectAuthCodeErrorMessage;
    }
    
    public String getAuthCodeMismatchErrorMessage() {
        return authCodeMismatchErrorMessage;
    }
    
    public String getAuthCodeWrongErrorMessage() {
        return authCodeWrongErrorMessage;
    }
    
    public String getAuthCodeVerifiedText() {
        return authCodeVerifiedText;
    }
    
    public String getAuthenticatedText() {
        return authenticatedText;
    }
    
    public String getBlankAuthError() {
        return blankAuthError;
    }
    
	public String getReCaptchaSiteKey() {
        return recaptchaConfigService.getRecaptchaSiteKey();
    }
	
	public String getReCaptchaError() {
        return reCaptchaError;
    }

    public String getCaptchaRequiredCheckbox() {
        return captchaRequiredCheckbox;
    }

    public String getCheckIconQrCodePath() {
        return checkIconQrCodePath;
    }
     public String getEmailIdMandatoryErrorMessage() {
        return emailIdMandatoryErrorMessage;
    }
	
	 public String getReportIssueTitletext() {
        return reportIssueTitletext;
    }
}
