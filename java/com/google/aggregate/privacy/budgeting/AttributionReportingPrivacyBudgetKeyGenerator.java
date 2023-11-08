/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.aggregate.privacy.budgeting;

import com.google.aggregate.adtech.worker.model.SharedInfo;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AttributionReportingPrivacyBudgetKeyGenerator implements PrivacyBudgetKeyGenerator {

  @Override
  public Optional<String> generatePrivacyBudgetKey(SharedInfo sharedInfo) {
    return Optional.of(createPrivacyBudgetKey(sharedInfo));
  }

  /**
   * Returns privacy budget key for sharedInfo with VERSION_0_1 using hash of following shared Info
   * fields- api, version, reporting_origin, destination and source_registration_time.
   */
  private String createPrivacyBudgetKey(SharedInfo sharedInfo) {
    List<String> privacyBudgetKeyInputElements = new LinkedList<>();
    privacyBudgetKeyInputElements.add(sharedInfo.api().get());
    privacyBudgetKeyInputElements.add(sharedInfo.version());
    privacyBudgetKeyInputElements.add(sharedInfo.reportingOrigin());
    privacyBudgetKeyInputElements.add(sharedInfo.destination().get());
    // Source registration time might not be present in every valid ARA report.
    sharedInfo
        .sourceRegistrationTime()
        .ifPresent(time -> privacyBudgetKeyInputElements.add(time.toString()));

    String privacyBudgetKeyHashInput =
        String.join(PRIVACY_BUDGET_KEY_DELIMITER, privacyBudgetKeyInputElements);

    return Hashing.sha256()
        .newHasher()
        .putBytes(privacyBudgetKeyHashInput.getBytes(StandardCharsets.UTF_8))
        .hash()
        .toString();
  }
}
