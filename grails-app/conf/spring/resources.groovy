import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.upload.RegistrationUploader
import org.bwpl.registration.validation.Validator

// Place your Spring DSL code here
beans = {

    securityUtils(SecurityUtils) {
        springSecurityService = ref("springSecurityService")
    }

    nav(NavItems) {
        securityUtils = ref("securityUtils")
    }

    registrationUploader(RegistrationUploader) {
        securityUtils = ref("securityUtils")
    }

    validator(Validator) {
        securityUtils = ref("securityUtils")
    }
}
