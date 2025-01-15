CREATE INDEX "address_ix" ON profile (address_id);

CREATE INDEX "profile_interest_ix" ON profile_interest (profile_id);

CREATE INDEX "profile_ix" ON profile_picture (profile_id);
