package com.example.config.service

import com.example.account.Account
import com.example.account.AccountRepository
import com.example.account.UserNotFoundException
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.ofType
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class ReactiveUserDetailsServiceImpl(private val accountRepository: AccountRepository) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            accountRepository.findByname(username)?.let(::CustomUserDetails)
        }.switchIfEmpty { UserNotFoundException("not found user name : $username").toMono() }
            .ofType()
    }

    class CustomUserDetails(val account: Account) : UserDetails {

        override fun getPassword(): String = account.password

        private fun authorities() =

            mutableListOf(SimpleGrantedAuthority("ROLE_USER")).apply {

                if (account.username == "wonwoo") {

                    this.add(SimpleGrantedAuthority("ROLE_ADMIN"))

                }
            }

        override fun getAuthorities() = authorities()

        override fun getUsername(): String = account.username

        override fun isAccountNonExpired(): Boolean = true

        override fun isAccountNonLocked(): Boolean = true

        override fun isCredentialsNonExpired(): Boolean = true

        override fun isEnabled(): Boolean = true
    }

}