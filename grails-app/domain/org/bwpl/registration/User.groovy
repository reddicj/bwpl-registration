package org.bwpl.registration

import org.apache.commons.lang.StringUtils

class User {

	transient springSecurityService

    String firstname
    String lastname
	String username
	String password

	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

    static User registrationSecretary = null
    static belongsTo = [club: Club]

	static constraints = {
        firstname nullable: true
        lastname nullable: true
		username blank: false, unique: true, email: true
		password blank: false
        club nullable: true
	}

	static mapping = {
        cache true
		password column: '`password`'
	}

    String getEmail() {
        return username
    }

    String getName() {

        StringBuilder sb = new StringBuilder()
        if (StringUtils.isNotEmpty(firstname)) {
            sb << firstname
        }
        if (StringUtils.isNotEmpty(lastname)) {
            sb << " " << lastname
        }
        String name = sb.toString().trim()
        if (StringUtils.isNotEmpty(name)) {
            return name
        }
        else {
            return username
        }
    }

    boolean hasRole(String role) {

        Role r = Role.findByAuthority(role)
        if (r == null) return false
        return authorities.contains(r)
    }

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {

        // dirty hack to prevent the RegisterController in the Spring Security UI plugin double encoding passwords
        if (password && password.length() > 16) return
		password = springSecurityService.encodePassword(password)
	}

    boolean equals(o) {

        if (this.is(o)) return true
        if (getClass() != o.class) return false
        User user = (User) o
        if (id != user.id) return false
        return true
    }

    int hashCode() {
        return id.hashCode()
    }

    String toString() {
        return name
    }
}
