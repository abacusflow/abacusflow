import type { ColumnType } from "ant-design-vue/es/table";
import type { DefaultRecordType } from "ant-design-vue/es/vc-table/interface";

type ExtraKeys = "action"; // 根据实际扩展
interface StrictColumnType<T> extends Omit<ColumnType<T>, "key"> {
  key: keyof T | ExtraKeys;
}

type StrictColumnsType<RecordType = DefaultRecordType> = (
  | StrictColumnGroupType<RecordType>
  | StrictColumnType<RecordType>
)[];

interface StrictColumnGroupType<RecordType>
  extends Omit<StrictColumnType<RecordType>, "dataIndex"> {
  children: StrictColumnsType<RecordType>;
}

export type {
  StrictColumnType as StrictTableColumnType,
  StrictColumnsType as StrictTableColumnsType
};
