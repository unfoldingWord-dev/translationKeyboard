class InitialDbLoad < ActiveRecord::Migration
  def change

    create_table "unicode_characters" do |t|
      t.string "englishDesc"
      t.integer "utf8hex"
      t.timestamps
    end
    create_table "characters", force: true do |t|
      t.integer  "modmask"
      t.integer  "sortnumber"
      t.datetime "created_at"
      t.datetime "updated_at"
      t.integer  "key_position_id"
      t.integer  "unicode_character_id"
    end

    create_table "key_positions", force: true do |t|
      t.integer  "column_index"
      t.integer  "row_index"
      t.float    "percent_width"
      t.datetime "created_at"
      t.datetime "updated_at"
      t.integer  "keyboard_variant_id"
    end

    create_table "keyboard_types", force: true do |t|
      t.string   "name"
      t.datetime "created_at"
      t.datetime "updated_at"
    end

    create_table "keyboard_variants", force: true do |t|
      t.string   "name"
      t.integer  "keyboard_type_id"
      t.datetime "created_at"
      t.datetime "updated_at"
      t.integer  "keyboard_id"
    end

    create_table "keyboards", force: true do |t|
      t.string   "name"
      t.datetime "created_at"
      t.datetime "updated_at"
    end
  end
end
