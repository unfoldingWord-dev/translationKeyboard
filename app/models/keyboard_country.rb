class KeyboardCountry < ActiveRecord::Base
	has_many :lang_regions
  	has_many :keyboard_languages, through: :lang_regions
end
