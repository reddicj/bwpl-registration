package org.bwpl.registration.utils

import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy

class BwplProperties {

    private final PropertiesConfiguration configuration

    BwplProperties() {

        URL bwplPropertiesFile = this.getClass().getResource("/bwpl.properties")
        configuration = new PropertiesConfiguration(bwplPropertiesFile.getFile())
        configuration.setReloadingStrategy(new FileChangedReloadingStrategy())
    }

    void refresh() {
        configuration.refresh()
    }

    List<String> getEmailListWeekendRegistrationsReport() {
        return new ArrayList<String>(configuration.getList("email.list.weekend.registrations.report"))
    }

    List<String> getEmailListWeeklyValidationReport() {
        return new ArrayList<String>(configuration.getList("email.list.weekly.validation.report"))
    }

    List<String> getEmailListNightlyDataExport() {
        return new ArrayList<String>(configuration.getList("email.list.nightly.data.export"))
    }

    String toHtmlString() {

        StringBuilder sb = new StringBuilder()
        sb << configuration.getFile().getPath() << "<br/>"
        sb << "email.list.weekend.registrations.report = " << emailListWeekendRegistrationsReport.join(", ") << "<br/>"
        sb << "email.list.weekly.validation.report = " << emailListWeeklyValidationReport.join(", ") << "<br/>"
        sb << "email.list.nightly.data.export = " << emailListNightlyDataExport.join(", ")
        return sb.toString()
    }
}
