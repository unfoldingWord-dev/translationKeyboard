class LangRegions < ActiveRecord::Base
  belongs_to :keyboardCountry
  belongs_to :keyboardLanguages
end
