# == Schema Information
#
# Table name: key_positions
#
#  id                  :integer          not null, primary key
#  column_index        :integer
#  row_index           :integer
#  percent_width       :float
#  created_at          :datetime
#  updated_at          :datetime
#  keyboard_variant_id :integer
#

class KeyPosition < ActiveRecord::Base
	has_many :characters
	belongs_to :keyboard_variant
end
