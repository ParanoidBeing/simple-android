package org.simple.clinic.patient

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Single
import org.simple.clinic.BuildConfig
import org.simple.clinic.patient.businessid.BusinessId
import org.simple.clinic.patient.businessid.BusinessIdMeta
import org.simple.clinic.patient.businessid.BusinessIdMetaAdapter
import org.simple.clinic.patient.businessid.MoshiBusinessIdMetaAdapter
import org.simple.clinic.patient.filter.SearchPatientByName
import org.simple.clinic.patient.filter.SortByWeightedNameParts
import org.simple.clinic.patient.filter.WeightedLevenshteinSearch
import org.simple.clinic.patient.fuzzy.AgeFuzzer
import org.simple.clinic.patient.fuzzy.PercentageFuzzer
import org.simple.clinic.phone.PhoneNumberMaskerConfig
import org.simple.clinic.util.UtcClock

@Module
open class PatientModule {

  @Provides
  open fun provideAgeFuzzer(utcClock: UtcClock): AgeFuzzer = PercentageFuzzer(utcClock = utcClock, fuzziness = 0.2F)

  @Provides
  open fun provideFilterPatientByName(): SearchPatientByName = WeightedLevenshteinSearch(
      minimumSearchTermLength = 3,
      maximumAllowedEditDistance = 350F,

      // Values are taken from what sqlite spellfix uses internally.
      characterSubstitutionCost = 150F,
      characterDeletionCost = 100F,
      characterInsertionCost = 100F,

      resultsComparator = SortByWeightedNameParts())

  @Provides
  open fun providePatientConfig(): Observable<PatientConfig> = Observable.just(PatientConfig(
      limitOfSearchResults = 100,
      scanSimpleCardFeatureEnabled = false,
      recentPatientLimit = 10
  ))

  @Provides
  open fun phoneNumberMaskerConfig(): Single<PhoneNumberMaskerConfig> =
      Single.just(PhoneNumberMaskerConfig(
          maskingEnabled = false,
          phoneNumber = BuildConfig.MASKED_PHONE_NUMBER
      ))

  @Provides
  fun provideBusinessIdMetaAdapter(moshi: Moshi): BusinessIdMetaAdapter {
    @Suppress("UNCHECKED_CAST")
    val adapters: Map<BusinessId.MetaVersion, JsonAdapter<BusinessIdMeta>> = mapOf(
        BusinessId.MetaVersion.BpPassportV1 to
            moshi.adapter(BusinessIdMeta.BpPassportV1::class.java) as JsonAdapter<BusinessIdMeta>
    )

    return MoshiBusinessIdMetaAdapter(adapters)
  }
}
