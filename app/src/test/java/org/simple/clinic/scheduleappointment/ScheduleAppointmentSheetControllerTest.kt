package org.simple.clinic.scheduleappointment

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.simple.clinic.overdue.AppointmentRepository
import org.simple.clinic.patient.PatientMocker
import org.simple.clinic.util.RxErrorsRule
import org.simple.clinic.widgets.UiEvent
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset.UTC
import org.threeten.bp.temporal.ChronoUnit
import java.util.UUID

@RunWith(JUnitParamsRunner::class)
class ScheduleAppointmentSheetControllerTest {

  @get:Rule
  val rxErrorsRule = RxErrorsRule()

  private val sheet = mock<ScheduleAppointmentSheet>()
  private val repository = mock<AppointmentRepository>()

  private val uiEvents = PublishSubject.create<UiEvent>()
  private val uuid = UUID.randomUUID()

  lateinit var controller: ScheduleAppointmentSheetController

  @Before
  fun setUp() {
    controller = ScheduleAppointmentSheetController(repository)

    uiEvents.compose(controller).subscribe { uiChange -> uiChange(sheet) }
  }

  @Test
  fun `when sheet is created, a date should immediately be displayed to the user`() {
    val current = 17
    uiEvents.onNext(ScheduleAppointmentSheetCreated(current, uuid, 20))

    verify(sheet).updateDisplayedDate(current)
  }

  @Test
  @Parameters(method = "paramsForIncrementingAndDecrementing")
  fun `when increment button is clicked, appointment due date should increase`(
      current: Int,
      size: Int
  ) {
    uiEvents.onNext(AppointmentDateIncremented(current, size))

    if (current == 0) {
      verify(sheet).updateDisplayedDate(current + 1)
      verify(sheet).enableDecrementButton(true)
    }

    if (current != size - 2) {
      verify(sheet).updateDisplayedDate(current + 1)
      verify(sheet).enableIncrementButton(true)
    }

    if (current == size - 2) {
      verify(sheet).updateDisplayedDate(current + 1)
      verify(sheet).enableIncrementButton(false)
    }

    if (current == size - 1) {
      verify(sheet, never()).updateDisplayedDate(any())
      verify(sheet, never()).enableIncrementButton(any())
      verify(sheet, never()).enableDecrementButton(any())
    }
  }

  @Test
  @Parameters(method = "paramsForIncrementingAndDecrementing")
  fun `when decrement button is clicked, appointment due date should decrease`(
      current: Int,
      size: Int
  ) {
    uiEvents.onNext(AppointmentDateDecremented(current, size))

    if (current == 0) {
      verify(sheet, never()).updateDisplayedDate(any())
      verify(sheet, never()).enableIncrementButton(any())
      verify(sheet, never()).enableDecrementButton(any())
    }

    if (current == 1) {
      verify(sheet).updateDisplayedDate(current - 1)
      verify(sheet).enableDecrementButton(false)
    }

    if (current != 0) {
      verify(sheet).updateDisplayedDate(current - 1)
    }

    if (current == size - 1) {
      verify(sheet).updateDisplayedDate(current - 1)
      verify(sheet).enableIncrementButton(true)
    }
  }

  fun paramsForIncrementingAndDecrementing() = arrayOf(
      arrayOf(3, 9),
      arrayOf(1, 9),
      arrayOf(7, 9),
      arrayOf(2, 9)
  )

  @Test
  fun `when not-now is clicked, appointment should not be scheduled, and sheet should dismiss`() {
    uiEvents.onNext(SchedulingSkipped())

    verify(repository, never()).schedule(any(), any())
    verify(sheet).closeSheet()
  }

  @Test
  fun `when done is clicked, appointment should be scheduled with the correct due date`() {
    whenever(repository.schedule(any(), any())).thenReturn(Single.just(PatientMocker.appointment()))

    val current = ScheduleAppointment("1 month", 1, ChronoUnit.MONTHS)
    val date = LocalDate.now(UTC).plus(1, ChronoUnit.MONTHS)
    uiEvents.onNext(ScheduleAppointmentSheetCreated(3, uuid, 4))
    uiEvents.onNext(AppointmentScheduled(current))

    verify(repository).schedule(uuid, date)
    verify(sheet).closeSheet(date)
  }

  @Test
  fun `when last appointment date is chosen on sheet creation, then increment button should be disabled`() {
    uiEvents.onNext(ScheduleAppointmentSheetCreated(
        defaultDateIndex = 3,
        patientUuid = uuid,
        numberOfDates = 4
    ))

    verify(sheet).enableIncrementButton(false)
  }

  @Test
  fun `when not-last appointment date is chosen on sheet creation, then increment button should be enabled`() {
    uiEvents.onNext(ScheduleAppointmentSheetCreated(
        defaultDateIndex = 2,
        patientUuid = uuid,
        numberOfDates = 4
    ))

    verify(sheet).enableIncrementButton(true)
  }

  @Test
  fun `when first appointment date is chosen on sheet creation, then decrement button should be disabled`() {
    uiEvents.onNext(ScheduleAppointmentSheetCreated(
        defaultDateIndex = 0,
        patientUuid = uuid,
        numberOfDates = 4
    ))

    verify(sheet).enableDecrementButton(false)
  }

  @Test
  fun `when not-first appointment date is chosen on sheet creation, then decrement button should be enabled`() {
    uiEvents.onNext(ScheduleAppointmentSheetCreated(
        defaultDateIndex = 1,
        patientUuid = uuid,
        numberOfDates = 4
    ))

    verify(sheet).enableDecrementButton(true)
  }
}
