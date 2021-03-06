package org.simple.clinic.protocol

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.simple.clinic.patient.PatientMocker
import org.simple.clinic.protocol.sync.ProtocolSync
import org.simple.clinic.user.UserSession
import org.simple.clinic.util.Just
import org.simple.clinic.util.None
import org.simple.clinic.util.RxErrorsRule

class SyncProtocolsOnLoginTest {

  @get:Rule
  val rxErrorsRule = RxErrorsRule()

  private val userSession = mock<UserSession>()
  private val protocolSync = mock<ProtocolSync>()
  private val protocolRepository = mock<ProtocolRepository>()

  private lateinit var syncProtocolOnLogin: SyncProtocolsOnLogin

  @Before
  fun setUp() {
    syncProtocolOnLogin = SyncProtocolsOnLogin(userSession, protocolSync, protocolRepository)
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
  }

  @Test
  fun `when user is not available then protocol drugs should not be synced`() {
    whenever(userSession.loggedInUser()).thenReturn(Observable.just(None))
    whenever(protocolRepository.recordCount()).thenReturn(Observable.just(1))

    syncProtocolOnLogin.listen()

    verify(protocolSync, never()).sync()
  }

  @Test
  fun `when user is available and existing drugs are empty then protocol drugs should be sync`() {
    whenever(userSession.loggedInUser()).thenReturn(Observable.just(
        None,
        Just(PatientMocker.loggedInUser())))
    whenever(protocolRepository.recordCount()).thenReturn(Observable.just(0))

    syncProtocolOnLogin.listen()

    verify(protocolSync, times(1)).sync()
  }

  @Test
  fun `when user is available and existing drugs are not empty then protocol drugs should not be synced`() {
    whenever(userSession.loggedInUser()).thenReturn(Observable.just(
        None,
        Just(PatientMocker.loggedInUser())))
    whenever(protocolRepository.recordCount()).thenReturn(Observable.just(1))

    syncProtocolOnLogin.listen()

    verify(protocolSync, never()).sync()
  }
}
