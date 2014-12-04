# app/views/api/v1/keyboard/index.json.rabl

object @a_keyboard
attributes :id => :keyboard_id, :name => :keyboard_name, :created_at_epoch => :created_at, :updated_at_epoch => :updated_at
attributes :iso_region, :iso_language
child :keyboard_variants, :object_root => false do
  attributes :name
  attributes :created_at_epoch => :created_at, :updated_at_epoch => :updated_at
  node :key_position_rows do |row|
    row.key_position_rows.map { |c|

        { :key_position_columns => partial("api/v1/keyboard/_key_pos", :object => c) }
    }

  end
end